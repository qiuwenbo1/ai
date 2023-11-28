package org.qwb.ai.faceRecognition.vo;

import lombok.Data;

import java.util.List;

@Data
public class FaceRecResultVO {

    private String image;

    private String labelImage;

    private List<FaceRecVO> faces;

}
