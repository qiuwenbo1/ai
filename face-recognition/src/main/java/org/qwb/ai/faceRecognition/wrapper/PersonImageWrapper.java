package org.qwb.ai.faceRecognition.wrapper;

import cn.hutool.core.bean.BeanUtil;
import org.qwb.ai.common.support.BaseEntityWrapper;
import org.qwb.ai.common.utils.SpringUtil;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.entity.PersonImage;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.vo.PersonImageVO;

import java.util.List;

public class PersonImageWrapper extends BaseEntityWrapper<PersonImage, PersonImageVO> {

    private static FaceImageRepository faceImageRepository;
    private static IOssEndPoint ossEndPoint;

    static {
        faceImageRepository = SpringUtil.getBean(FaceImageRepository.class);
        ossEndPoint = SpringUtil.getBean(IOssEndPoint.class);
    }

    public static PersonImageWrapper build() {
        return new PersonImageWrapper();
    }


    @Override
    public PersonImageVO entityVO(PersonImage entity) {
        PersonImageVO vo = BeanUtil.copyProperties(entity, PersonImageVO.class);

        vo.setUrl(ossEndPoint.fileLink(vo.getImageFile()).getData());
        List<FaceImage> faceImages = faceImageRepository.findByImage(entity.getId());
        vo.setFaces(FaceImageWrapper.build().listVO(faceImages));

        return vo;
    }
}
