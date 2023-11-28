package org.qwb.ai.faceRecognition.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class FaceImageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long person;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long image;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long attach;

    private String imageFile;

    private String url;

    private int x;

    private int y;

    private int w;

    private int h;

}
