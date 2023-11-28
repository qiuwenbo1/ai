package org.qwb.ai.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDetectionLabel {

    private int x;

    private int y;

    private int w;

    private int h;

    private String label;

    private Double conf;

    private String recImg;

}
