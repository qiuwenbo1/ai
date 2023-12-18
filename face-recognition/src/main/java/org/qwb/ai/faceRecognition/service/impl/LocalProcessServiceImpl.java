package org.qwb.ai.faceRecognition.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import feign.Response;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.faceRecognition.dto.FaceCompareDto;
import org.qwb.ai.faceRecognition.dto.FaceInfoDto;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.entity.FaceStorage;
import org.qwb.ai.faceRecognition.entity.Person;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.repository.FaceStorageRepository;
import org.qwb.ai.faceRecognition.repository.PersonRepository;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.service.ILocalProcessService;
import org.qwb.ai.faceRecognition.utils.InMemoryMultipartFile;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LocalProcessServiceImpl implements ILocalProcessService {
    @Resource
    private FaceStorageRepository faceStorageRepository;
    @Resource
    private FaceService insightFaceService;
    @Resource
    private FaceUploadOss faceUploadOss;
    @Resource
    private PersonRepository personRepository;
    @Resource
    private IOssEndPoint iOssEndPoint;
    @Resource
    private FaceImageRepository faceImageRepository;

    @Override
    public List<FaceStorage> isProcessed(String parentPath) {
        List<FaceStorage> processedFiles = new ArrayList<>();
        boolean b = FileUtil.isFile(parentPath);
        if (b) {
            processedFiles.add(faceStorageRepository.findByParentPathAndOriginName(FileUtil.getParent(parentPath, 1), FileUtil.getName(parentPath)));
        } else {
            processedFiles = faceStorageRepository.findByParentPath(parentPath);
        }
        return processedFiles;
    }

    public void process(String sourPath, String outPath) {
        List<File> files = FileUtil.loopFiles(sourPath);
        Map<String, List<File>> collect = files.stream().collect(Collectors.groupingBy(File::getParent));
        for (Map.Entry<String, List<File>> entry : collect.entrySet()) {
            String key = entry.getKey();
            List<File> fileList = entry.getValue();
            List<FaceStorage> storages = isProcessed(key);
            if (fileList.size() == storages.size()) continue;
            Map<File, PersonAndFace> detectMap = fileDetect(fileList);
            compareFace(detectMap, outPath);
        }
    }

    /**
     * 人脸检测
     *
     * @param files 需要检测是否有人脸的文件
     */
    public Map<File, PersonAndFace> fileDetect(List<File> files) {
        Map<File, PersonAndFace> result = new HashMap<>();
        ArrayList<File> list = new ArrayList<>();
        List<String> links = new ArrayList<>();
        ArrayList<Attach> attaches = new ArrayList<>();
        for (File file : files) {
            Attach attach = faceUploadOss.faceUpload(file);
            if (attach == null) continue;
            links.add(iOssEndPoint.fileLink(attach.getName()).getData());
            list.add(file);
            attaches.add(attach);
        }
        List<List<FaceRecVO>> recVOS = insightFaceService.detectPlusByLinks(links);
        for (int i = 0; i < list.size(); i++) {
            List<FaceRecVO> faceRecVOS = recVOS.get(i);
            if (faceRecVOS.isEmpty()){
                buildFaceStorage(list.get(i),false,attaches.get(i));
                continue;
            }
            result.put(list.get(i), new PersonAndFace(attaches.get(i), faceRecVOS));
        }
        return result;
    }


    private void compareFace(Map<File, PersonAndFace> detectMap, String outPath) {
        for (Map.Entry<File, PersonAndFace> entry : detectMap.entrySet()) {
            File file = entry.getKey();
            //识别到的人脸，集合为空则该照片没有人脸
            List<FaceRecVO> recVOS = entry.getValue().getFaceRecVOS();
            Attach fileAttach = entry.getValue().getAttach();
            try (InputStream fileInputStream = new FileInputStream(file)) {
                //对识别出来的人脸进行人脸库比对
                for (FaceRecVO recVO : recVOS) {
                    List<FaceCompareDto> compareDtos = insightFaceService.faceRecognition(recVO.getVec(), 0.75f);
                    //如果人脸库没有存储该人脸特征信息，则创建该人脸特征信息库
                    if (compareDtos.isEmpty()) {
                        FaceImage faceImage = buildFaceFeatureFolder(file, recVO, fileAttach, outPath);
                        insightFaceService.addFeatureToRedis(new FaceInfoDto(faceImage.getId(),recVO.getVec()));
                    }
                }
            } catch (Exception e) {
                log.error("【{}】---【{}】", LocalProcessServiceImpl.log, e.getMessage());
            }
            buildFaceStorage(file,true,fileAttach);
        }
    }

    /**
     * 构建本地文件和数据库的关联，并记录此文件是否有人脸
     *
     * @param file     本地文件
     * @param hasFaces 是否有人脸
     * @param attach   文件的minio ID
     */
    void buildFaceStorage(File file, Boolean hasFaces, Attach attach) {
        FaceStorage faceStorage = new FaceStorage();
        faceStorage.setHasFace(hasFaces);
        faceStorage.setAttachId(attach.getId());
        faceStorage.setParentPath(file.getParent());
        faceStorage.setOriginName(file.getName());
        faceStorageRepository.save(faceStorage);
    }

    FaceImage buildFaceFeatureFolder(File file, FaceRecVO recVO, Attach faceImageAttach, String outPath) {
        FaceImage faceImage = new FaceImage();
        String dirPath = outPath + File.pathSeparator + IdUtil.randomUUID();
        File personPath = FileUtil.isDirectory(dirPath) ? new File(dirPath) : FileUtil.mkdir(dirPath);
        File faceFile = new File(personPath.getPath() + File.pathSeparator + FileUtil.getPrefix(file) + IdUtil.randomUUID() + "-face." + FileUtil.getSuffix(file));
        ImgUtil.cut(
                file,
                faceFile,
                new Rectangle(recVO.getX(), recVO.getY(), recVO.getW(), recVO.getH())
        );
        Attach attach = faceUploadOss.faceUpload(faceFile);
        Person person = new Person();
        person.setName(personPath.getName());
        person.setLocalFolderPath(personPath.getAbsolutePath());
        Person person1 = personRepository.save(person);
        faceImage.setPersonId(person1.getId());
        faceImage.setX(recVO.getX());
        faceImage.setY(recVO.getY());
        faceImage.setW(recVO.getW());
        faceImage.setH(recVO.getH());
        faceImage.setOriginalAttachId(faceImageAttach.getId());
        faceImage.setOriginalAttachName(faceImageAttach.getName());
        faceImage.setFaceAttachId(attach.getId());
        faceImage.setFaceAttachName(attach.getName());
        return faceImageRepository.save(faceImage);
    }

    @Getter
    @Setter
    @Data
    public static class PersonAndFace {
        //有人脸的原图片
        private Attach attach;
        //人脸特征集合
        private List<FaceRecVO> faceRecVOS;

        public PersonAndFace(Attach attach, List<FaceRecVO> faceRecVOS) {
            this.attach = attach;
            this.faceRecVOS = faceRecVOS;
        }
    }
}
