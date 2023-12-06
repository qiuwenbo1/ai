package org.qwb.ai.faceRecognition.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
