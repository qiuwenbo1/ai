package org.qwb.ai.faceRecognition.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.qwb.ai.faceRecognition.dto.FaceRectangle;

import java.util.List;

@Data
public class FaceRecVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;

    private String name;

    private float similar;

    private int X;
    private int Y;
    private int W;
    private int H;
    private List<Float> vec;

    public FaceRecVO() {
    }

    public FaceRecVO(int left, int top, int right, int bottom) {
        this.setX(left);
        this.setY(top);
        this.setW(right - left);
        this.setH(bottom - top);
    }


}
