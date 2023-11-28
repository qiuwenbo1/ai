package org.qwb.ai.faceRecognition.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.qwb.ai.faceRecognition.dto.FaceRectangle;

@Data
public class FaceRecVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;

    private String name;

    private float similar;

    private FaceRectangle rect;



}
