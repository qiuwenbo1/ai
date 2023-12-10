package org.qwb.ai.faceRecognition.wrapper;

import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.common.support.BaseEntityWrapper;
import org.qwb.ai.common.utils.SpringUtil;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.qwb.ai.faceRecognition.vo.PersonVO;


public class FaceInfoWrapper extends BaseEntityWrapper<FaceRecVO, PersonVO> {

    private static IOssEndPoint ossEndPoint;

    public static FaceInfoWrapper build() {
        return new FaceInfoWrapper();
    }

    static {
        ossEndPoint = SpringUtil.getBean(IOssEndPoint.class);
    }

    public PersonVO entityVOwithAttach(FaceRecVO entity, Attach attach) {
        PersonVO vo = entityVO(entity);
        vo.setCoverFile(attach.getId());
        vo.setCoverSrc(attach.getLink());
        return vo;
    }

    public PersonVO entityVOwithAttach(FaceRecVO entity, Long attachId) {
        Attach attach = ossEndPoint.attachById(attachId).getData();
        return entityVOwithAttach(entity, attach);
    }

    @Override
    public PersonVO entityVO(FaceRecVO entity) {
        PersonVO vo = new PersonVO();
        vo.setX(entity.getX());
        vo.setY(entity.getY());
        vo.setW(entity.getW());
        vo.setH(entity.getH());

        return vo;
    }
}
