package org.qwb.ai.faceRecognition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ai_cv_person_image")
@Getter
@Setter
@ToString
public class PersonImage extends AbstractBaseEntity {

    /**
     * 文件ID
     */
    private Long file;

    /**
     * 人物ID
     */
    private Long person;

    /**
     * 图url
     */
    @Column(name = "image_file")
    private String imageFile;


}
