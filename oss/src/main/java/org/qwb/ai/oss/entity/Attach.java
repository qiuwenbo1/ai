package org.qwb.ai.oss.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.qwb.ai.common.utils.SnowflakeId;

import javax.persistence.*;

@Data
@Getter
@Setter
@Entity
@Table(name = "ai_attach")
@NoArgsConstructor
public class Attach {

	@Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.qwb.ai.common.utils.SnowflakeId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
	 * 附件地址
	 */
	private String link;
	/**
	 * 附件域名
	 */
	private String domain;
	/**
	 * 附件名称
	 */
	private String name;
	/**
	 * 附件原名
	 */
	@Column(name = "original_name")
	private String originalName;
	/**
	 * 附件拓展名
	 */
	private String extension;
	/**
	 * 附件大小
	 */
	@Column(name = "attach_size")
	private Long attachSize;
}
