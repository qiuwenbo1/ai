package org.qwb.ai.faceRecognition.wrapper;

import cn.hutool.core.bean.BeanUtil;
import org.qwb.ai.common.support.BaseEntityWrapper;
import org.qwb.ai.common.utils.SpringUtil;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.vo.FaceImageVO;

public class FaceImageWrapper extends BaseEntityWrapper<FaceImage, FaceImageVO> {

    private static IOssEndPoint ossEndPoint;

    public static FaceImageWrapper build() {
        return new FaceImageWrapper();
    }

    static {
        ossEndPoint = SpringUtil.getBean(IOssEndPoint.class);
    }


    @Override
    public FaceImageVO entityVO(FaceImage entity) {
        FaceImageVO vo = BeanUtil.copyProperties(entity, FaceImageVO.class);

        vo.setUrl(ossEndPoint.fileLink(entity.getImageFile()).getData());

        return vo;
    }
}
