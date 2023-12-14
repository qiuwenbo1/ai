package org.qwb.ai.faceRecognition.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 本地文件和minio关联表
 * 用于记录该文件是否已经被处理过
 */
@Entity
@Table(name = "ai_cv_face_storage")
@Getter
@Setter
@ToString
public class FaceStorage extends AbstractBaseEntity{

    /**
     * 文件父级路径
     */
    private String parentPath;
    /**
     * 文件名
     */
    private String originName;

    private Long attachId;

    private Boolean hasFace;

}
