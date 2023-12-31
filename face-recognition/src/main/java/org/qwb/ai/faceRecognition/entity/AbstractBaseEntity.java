package org.qwb.ai.faceRecognition.entity;

import java.io.Serializable;
import java.util.Date;


import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.GenericGenerator;
import org.qwb.ai.common.pojo.BaseStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
//@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class AbstractBaseEntity implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.qwb.ai.common.utils.SnowflakeId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    @JsonSerialize(using = ToStringSerializer.class)
    @CreatedBy
    private Long createUser;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, name = "create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "update_user")
    @LastModifiedBy
    private Long updateUser;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @LastModifiedDate
    private Date updateTime;

    /**
     * 状态[1:正常]
     */
    private String status = BaseStatus.OK;

    /**
     * 状态[0:未删除,1:删除]
     */
    @Column(name = "is_deleted")
    private String isDeleted = BaseStatus.OK;

}

