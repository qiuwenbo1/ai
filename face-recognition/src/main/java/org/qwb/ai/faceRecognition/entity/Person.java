package org.qwb.ai.faceRecognition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "ai_cv_person")
@Getter
@Setter
@ToString
public class Person extends AbstractBaseEntity {

    private String name;

    @Column(name = "cover_file")
    private Long coverFile;

    /**
     * 人物的裁剪人脸
     */
    @Column(name = "face_file")
    private Long faceFile;

    private String description;
}
