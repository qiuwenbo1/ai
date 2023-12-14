package org.qwb.ai.faceRecognition.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.file.FileNameUtil;
import feign.Response;
import lombok.extern.log4j.Log4j2;
import org.qwb.ai.common.api.R;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.common.support.CommonCondition;
import org.qwb.ai.common.support.Condition;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.entity.Person;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.repository.PersonRepository;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.utils.InMemoryMultipartFile;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.qwb.ai.faceRecognition.vo.PersonVO;
import org.qwb.ai.faceRecognition.wrapper.FaceInfoWrapper;
import org.qwb.ai.faceRecognition.wrapper.PersonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value = "/person")
public class PersonController{

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private FaceImageRepository faceImageRepository;
    @Autowired
    private IOssEndPoint ossEndPoint;
    @Resource
    private FaceService insightFaceService;

    @RequestMapping(value = "/info")
    public R<PersonVO> personById(@RequestParam String id) {
        Person person = personRepository.findById(Convert.toLong(id)).orElse(new Person());
        if (person.getId() == null){
            log.error("不存在的人物id：【{}】",id);
            return R.failed("不存在的人物");
        }
        return R.data(PersonWrapper.build().entityVO(person));
    }


    public void process(){
        //1. 遍历文件夹，对每张图片进行detect，将detect出的人脸与redis库进行比对。
        //2. 如果没有相似的，对detect出的图像进行保存在本地人脸库，原图则保存至同目录相关图片库。创建该人脸特征库，并存储至redis
        //3. 如果有相似的，将图片保存至对应人物的相关图片库
        //
        //
        String sourcePath = "D:\\file\\个人\\照片\\01.日常记录\\2023";


    }

    @RequestMapping(value = "/addRec")
    public R<PersonVO> addRec(MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > -1) {
            baos.write(buffer, 0, length);
        }
        baos.flush();

        List<FaceRecVO> faceInfos = insightFaceService.detect(file.getInputStream());
        System.out.println(faceInfos);
        if (faceInfos != null) {
            if (faceInfos.size() > 1) {
                return R.failed("检测人脸数量大于1");
            }
            if (faceInfos.size() == 0) {
                return R.failed("未检测出人脸");
            }
        } else {
            return R.success();
        }
        // 将文件传送至对象存储
        InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
        MultipartFile file2 = new InMemoryMultipartFile(file.getOriginalFilename(), stream2);
        long current1 = DateUtil.current();
        Attach attach = ossEndPoint.putAttach(file2).getData();
        long current2 = DateUtil.current();
        log.info("上传照片花费时间【{}ms】", current2 - current1);
        PersonVO personVO = FaceInfoWrapper.build().entityVOwithAttach(faceInfos.get(0), attach);
        return R.data(personVO);
    }

    /**
     * 新增人物
     * @param name
     * @param description
     * @param coverFile
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/submit")
    public R add(@RequestParam String name, @RequestParam(required = false, defaultValue = "") String description, @RequestParam Long coverFile, @RequestParam int x, @RequestParam int y,
                 @RequestParam int w, @RequestParam int h) throws IOException {
        // 添加人物
        Person person = new Person();
        person.setName(name);
        person.setDescription(description);
        person.setCoverFile(coverFile);
        personRepository.save(person);

        Attach originalImg = ossEndPoint.attachById(coverFile).getData();
        // 裁剪人脸图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream(37628);
        Response fileIns = ossEndPoint.getFileIns(originalImg.getName());
        ImgUtil.cut(
                fileIns.body().asInputStream(),
                baos,
                new Rectangle(x, y, w, h)
        );
        InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
        String originalName = originalImg.getOriginalName();
        String newName = FileNameUtil.getName(originalName) + "-face." + FileNameUtil.getSuffix(originalName);
        MultipartFile file2 = new InMemoryMultipartFile(newName, stream2);
        Attach faceAttach = ossEndPoint.putAttach(file2).getData();
        FaceImage faceImage = new FaceImage();
        faceImage.setOriginalAttachId(originalImg.getId());
        faceImage.setOriginalAttachName(originalImg.getName());
        faceImage.setFaceAttachId(faceAttach.getId());
        faceImage.setFaceAttachName(faceAttach.getName());
        faceImage.setX(x);
        faceImage.setY(y);
        faceImage.setW(w);
        faceImage.setH(h);
        faceImageRepository.save(faceImage);
        person.setFaceFile(faceAttach.getId());
        personRepository.save(person);

        return R.data(person);
    }


    /**
     * 人脸库数据集列表
     * @param person
     * @param pageable
     * @return
     */

    @RequestMapping(value = "/list")
    public R<Page<PersonVO>> datasetList(@RequestParam Map<String, Object> person,
                                         @PageableDefault(page = 0, size = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Person> list = personRepository.findAll(CommonCondition.getQuerySpecification(person, Person.class), pageable);
        return R.data(new PageImpl<>(PersonWrapper.build().listVO(list.getContent()), pageable, list.getTotalElements()));
    }

    /**
     * 列表
     * @param person
     * @return
     */
    @RequestMapping(value = "/list-all")
    public R<List<PersonVO>> datasetList(@RequestParam Map<String, Object> person) {
        List<Person> list = personRepository.findAll(Condition.getQuerySpesification(person, Person.class));
        return R.data(PersonWrapper.build().listVO(list));
    }

    /**
     * 获取所有人物
     * @param person
     * @return
     */
    @RequestMapping(value = "/list-faster")
    public R<List<Person>> listFaster(@RequestParam Map<String, Object> person) {
        List<Person> list = personRepository.findAll(Condition.getQuerySpesification(person, Person.class));
        return R.data(list);
    }

    /**
     * 修改人物
     * @param name
     * @param description
     * @param coverFile
     * @param personId
     * @return
     */
    @RequestMapping(value = "/update")
    public R edit(@RequestParam String name, @RequestParam(required = false, defaultValue = "") String description, @RequestParam String coverFile, @RequestParam Long personId) {
        Person person = personRepository.findById(personId).get();
        person.setName(name);
        person.setDescription(description);
        person.setCoverFile(Convert.toLong(coverFile));
        return R.data(personRepository.save(person));
    }

}
