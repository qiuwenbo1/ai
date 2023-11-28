package org.qwb.ai.faceRecognition.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 人物信息的封装
 */
@Data
public class PersonVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    /**
     * 人物的人脸特征图id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long faceFile;

    private String createUserName;

    /**
     *照片数量
     */
    private Long imageNum;

    /**
     *人物封面图链接
     */
    private String coverSrc;

    /**
     *人脸链接
     */
    private String faceSrc;

    /**
     *人物封面图id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long coverFile;

    private int x;

    private int y;

    private int w;

    private int h;

    private String createTime;

    private String status;

    private String description;

}
