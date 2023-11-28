package org.qwb.ai.faceRecognition.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class PersonImageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long file;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long person;

    private String imageFile;

    private String image;

    private List<FaceImageVO> faces;

    private String url;
}
