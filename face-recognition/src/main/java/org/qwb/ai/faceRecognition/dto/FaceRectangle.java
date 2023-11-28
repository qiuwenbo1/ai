package org.qwb.ai.faceRecognition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceRectangle {

    private int x;

    private int y;

    private int w;

    private int h;

}
