
package org.qwb.ai.common.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelServerStatus {

    private Long id;

    private String serverHealty;

    private String modelHealty;

    private JSONObject metaData;
    
    private String qaNerModelHealty;

    private String qaSimModelHealty;

    private JSONObject qaNerMetadata;

    private JSONObject qaSimMetadata;

    private String sensitiveClassifyHealty;

    private String sensitiveTokenHealty;

    private String sensitiveImageHealty;
}
