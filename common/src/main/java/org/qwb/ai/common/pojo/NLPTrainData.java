package org.qwb.ai.common.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NLPTrainData {

    private Long id;

    private String text;

    private String label;

}