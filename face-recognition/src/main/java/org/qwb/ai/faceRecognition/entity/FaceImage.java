package org.qwb.ai.faceRecognition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.qwb.ai.faceRecognition.dto.FaceRectangle;


@Entity
@Table(name = "ai_cv_face_image")
@Getter
@Setter
@ToString
public class FaceImage extends AbstractBaseEntity {

    /**
     * 人物，对应Person
     */
    private Long person;

    /**
     * 图片，对应PersonImage，未裁剪的原图,用于抽取人脸特征
     */
    private Long image;

    /**
     * 文件ID，裁剪后的人脸
     */
    private Long attach;

    /**
     * 裁剪后的人脸的url
     */
    @Column(name = "image_file")
    private String imageFile;

    private int x;

    private int y;

    private int w;

    private int h;

    public FaceRectangle getRectangle() {
        return  new FaceRectangle(x, y, w, h);
    }

}
