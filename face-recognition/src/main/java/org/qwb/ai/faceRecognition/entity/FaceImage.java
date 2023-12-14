package org.qwb.ai.faceRecognition.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.qwb.ai.faceRecognition.dto.FaceRectangle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "ai_cv_face_image")
@Getter
@Setter
@ToString
public class FaceImage extends AbstractBaseEntity {

    /**
     * 人物，对应Person
     */
    private Long personId;

    /**
     * 图片，对应PersonImage，未裁剪的原图,用于抽取人脸特征
     */
    private Long originalAttachId;
    private String originalAttachName;

    /**
     * 文件ID，裁剪后的人脸
     */
    private Long faceAttachId;

    /**
     * 裁剪后的人脸的url
     */
    private String faceAttachName;

    private int x;

    private int y;

    private int w;

    private int h;

    public FaceRectangle getRectangle() {
        return  new FaceRectangle(x, y, w, h);
    }

}
