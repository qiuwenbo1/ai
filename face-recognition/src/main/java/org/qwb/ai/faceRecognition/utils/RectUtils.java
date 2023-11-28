package org.qwb.ai.faceRecognition.utils;

import com.arcsoft.face.Rect;
import org.qwb.ai.faceRecognition.dto.FaceRectangle;

public class RectUtils {

    public static FaceRectangle convertArcFace(Rect rect) {
        FaceRectangle rectangle = new FaceRectangle();

        rectangle.setX(rect.getLeft());
        rectangle.setY(rect.getTop());
        rectangle.setW(rect.getRight() - rect.getLeft());
        rectangle.setH(rect.getBottom() - rect.getTop());

        return rectangle;
    }

}
