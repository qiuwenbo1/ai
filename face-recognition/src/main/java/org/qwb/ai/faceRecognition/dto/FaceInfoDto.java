package org.qwb.ai.faceRecognition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceInfoDto implements Serializable {

    public FaceInfoDto(Long id, byte[] faceFeature){
        this.id = id;
        this.faceFeature = faceFeature;
    }

    public FaceInfoDto(Long id, List<Float> insightFeature){
        this.id = id;
        this.insightFeature = insightFeature;
    }

    private Long id;

//    private String name;

    private byte[] faceFeature;

    private List<Float> insightFeature;
}
