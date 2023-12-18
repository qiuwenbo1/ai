package org.qwb.ai.faceRecognition.service;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import org.qwb.ai.faceRecognition.dto.FaceCompareDto;
import org.qwb.ai.faceRecognition.dto.FaceInfoDto;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface FaceService {

    /**
     * 人脸检测
     * @param urls 图片url地址
     */
    List<List<FaceRecVO>> detectPlusByLinks(List<String> urls);
    List<List<FaceRecVO>> detectPlusByBase64(List<String> base64);
    /**
     * 人脸检测
     * @param image 图片文件
     */
    List<FaceRecVO> detect(File image);

    /**
     * 人脸检测
     * @param ins 图片输入流
     */
    List<FaceRecVO> detect(InputStream ins);

    List<FaceRecVO> detect(InputStream ins,String imgType);

    List<FaceRecVO> detect(ImageInfo imageInfo);

    byte[] extractFaceFeature(ImageInfo imageInfo, FaceRecVO faceInfo);

    List<Float> extractFaceFeature(InputStream ins, FaceRecVO faceInfo);

    /**
     * 初始化人脸库
     */
    void initFaceRepo();

    /**
     * 添加人脸特征到redis
     * @param faceInfoDto 人脸特征
     */
    void addFeatureToRedis(FaceInfoDto faceInfoDto);


    List<FaceCompareDto> faceRecognition(byte[] faceFeature, float passRate);

    List<FaceCompareDto> faceRecognition(List<Float> faceFeature, float passRate);
    List<FaceCompareDto> faceRecognitions(List<Float> faceFeature, float passRate);

    List<FaceCompareDto> faceRecognition(InputStream ins, float passRate);

}
