package org.qwb.ai.faceRecognition.wrapper;

import com.arcsoft.face.FaceInfo;
import org.qwb.ai.common.pojo.Attach;
import org.qwb.ai.common.support.BaseEntityWrapper;
import org.qwb.ai.common.utils.SpringUtil;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.vo.PersonVO;


public class FaceInfoWrapper extends BaseEntityWrapper<FaceInfo, PersonVO> {

    private static IOssEndPoint ossEndPoint;

    public static FaceInfoWrapper build() {
        return new FaceInfoWrapper();
    }

    static {
        ossEndPoint = SpringUtil.getBean(IOssEndPoint.class);
    }

    public PersonVO entityVOwithAttach(FaceInfo entity, Attach attach) {
        PersonVO vo = entityVO(entity);
        vo.setCoverFile(attach.getId());
        vo.setCoverSrc(attach.getLink());
        return vo;
    }

    public PersonVO entityVOwithAttach(FaceInfo entity, Long attachId) {
        Attach attach = ossEndPoint.attachById(attachId).getData();
        return entityVOwithAttach(entity, attach);
    }

    @Override
    public PersonVO entityVO(FaceInfo entity) {
        PersonVO vo = new PersonVO();
        vo.setX(entity.getRect().getLeft());
        vo.setY(entity.getRect().getTop());
        vo.setW(entity.getRect().getRight() - entity.getRect().getLeft());
        vo.setH(entity.getRect().getBottom() - entity.getRect().getTop());

        return vo;
    }
}
