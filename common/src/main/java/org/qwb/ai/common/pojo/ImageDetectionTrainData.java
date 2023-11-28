package org.qwb.ai.common.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class ImageDetectionTrainData {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String url;

    private Integer width;

    private Integer height;

    private List<ImageDetectionLabel> labels;

}
