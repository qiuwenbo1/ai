package org.qwb.ai.faceRecognition.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.google.common.collect.Lists;
import org.qwb.ai.common.api.R;
import org.qwb.ai.common.pojo.AiCloudFile;
import org.qwb.ai.faceRecognition.dto.FaceCompareDto;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.entity.Person;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.repository.PersonRepository;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.utils.RectUtils;
import org.qwb.ai.faceRecognition.vo.FaceRecResultVO;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/face")
public class FaceController {

    @Resource
    private FaceService insightFaceService;
    @Resource
    private FaceImageRepository faceImageRepository;
    @Resource
    private PersonRepository personRepository;
    private final Logger logger = LoggerFactory.getLogger(FaceController.class);
    @Resource
    private IOssEndPoint iOssEndPoint;

    @RequestMapping(value = "/infer2")
    public R<FaceRecResultVO> infer(MultipartFile file) throws IOException {
        long timeStart = DateUtil.current();
        int personNum = 0;
        FaceRecResultVO result = new FaceRecResultVO();
        List<FaceRecVO> faceRecList = Lists.newLinkedList();

        List<FaceInfo> faceInfos;
        ImageInfo rgbData = new ImageInfo();

        // 复制文件流
        InputStream is = file.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > -1) {
            baos.write(buffer, 0, length);
        }
        baos.flush();

        faceInfos = insightFaceService.detect(file.getInputStream());

        if (CollectionUtil.isNotEmpty(faceInfos)) {
            for (FaceInfo faceInfo : faceInfos) {
                FaceRecVO faceRecVO = new FaceRecVO();
                faceRecVO.setRect(RectUtils.convertArcFace(faceInfo.getRect()));
                List<Float> feature = insightFaceService.extractFaceFeature(file.getInputStream(), faceInfo);
                if (feature != null) {
                    List<FaceCompareDto> faceCompareDtoList = insightFaceService.faceRecognition(feature, 0.75f);
                    if (CollectionUtil.isNotEmpty(faceCompareDtoList)) {
                        Long faceId = faceCompareDtoList.get(0).getId();
                        FaceImage faceImage = faceImageRepository.findById(faceId).orElse(null);
                        if (faceImage != null) {
                            Person person = personRepository.findById(faceImage.getPerson()).orElse(new Person());
                            faceRecVO.setName(person.getName());
                            faceRecVO.setPersonId(person.getId());
                            faceRecVO.setSimilar(faceCompareDtoList.get(0).getSimilar());

                            personNum++;

                        }
                    }
                }

                faceRecList.add(faceRecVO);

            }

        }


        result.setFaces(faceRecList);

        // 画图片
        InputStream ins2 = new ByteArrayInputStream(baos.toByteArray());
        Image img = ImgUtil.toImage(IoUtil.readBytes(ins2));
        String image = ImgUtil.toBase64DataUri(img, ImgUtil.IMAGE_TYPE_JPEG);
        result.setImage(image);

        // 画框
        InputStream ins3 = new ByteArrayInputStream(baos.toByteArray());
        BufferedImage labelImg = ImageIO.read(ins3);
        Graphics2D g = (Graphics2D) labelImg.getGraphics();
        int width = labelImg.getWidth() / 200;
        g.setStroke(new BasicStroke(width));
        g.setColor(Color.GREEN);
        for (FaceRecVO face : faceRecList) {
            g.drawRect(face.getRect().getX(), face.getRect().getY(), face.getRect().getW(), face.getRect().getH());
        }
        g.dispose();
        String labelImage = ImgUtil.toBase64DataUri(labelImg, ImgUtil.IMAGE_TYPE_JPEG);
        result.setLabelImage(labelImage);

        logger.info("完成人脸识别，识别了{}张人脸，{}个人物，耗时{}s", result.getFaces().size(), personNum, (DateUtil.current() - timeStart) / 1000);


        return R.data(result);
    }
}
