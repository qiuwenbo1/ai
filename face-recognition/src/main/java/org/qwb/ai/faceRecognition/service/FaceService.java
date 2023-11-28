package org.qwb.ai.faceRecognition.service;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import org.qwb.ai.faceRecognition.dto.FaceCompareDto;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface FaceService {

    /**
     * 人脸检测
     * @param image
     * @return
     */
    List<FaceInfo> detect(File image);

    List<FaceInfo> detect(InputStream ins);

    List<FaceInfo> detect(InputStream ins,String imgType);

    List<FaceInfo> detect(ImageInfo imageInfo);

    byte[] extractFaceFeature(ImageInfo imageInfo, FaceInfo faceInfo);

    List<Float> extractFaceFeature(InputStream ins, FaceInfo faceInfo);

    /**
     * 初始化人脸库
     */
    void initFaceRepo();


    List<FaceCompareDto> faceRecognition(byte[] faceFeature, float passRate);

    List<FaceCompareDto> faceRecognition(List<Float> faceFeature, float passRate);
    List<FaceCompareDto> faceRecognitions(List<Float> faceFeature, float passRate);

    List<FaceCompareDto> faceRecognition(InputStream ins, float passRate);

}
