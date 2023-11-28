package org.qwb.ai.faceRecognition.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.irm.ai.system.entity.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ai_cv_person_image")
@Getter
@Setter
@ToString
public class PersonImage extends AbstractBaseEntity {

    @ApiModelProperty(value = "文件ID")
    private Long file;

    @ApiModelProperty(value = "人物ID")
    private Long person;

    /**
     * 图url
     */
    @Column(name = "image_file")
    private String imageFile;


}
