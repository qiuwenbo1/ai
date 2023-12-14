package org.qwb.ai.faceRecognition.wrapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.common.support.BaseEntityWrapper;
import org.qwb.ai.common.utils.SpringUtil;
import org.qwb.ai.faceRecognition.entity.Person;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.vo.PersonVO;

public class PersonWrapper extends BaseEntityWrapper<Person, PersonVO> {
    private static FaceImageRepository faceImageRepository;
    private static IOssEndPoint ossEndPoint;


    public static PersonWrapper build() {
        return new PersonWrapper();
    }

    static {
        faceImageRepository = SpringUtil.getBean(FaceImageRepository.class);
        ossEndPoint = SpringUtil.getBean(IOssEndPoint.class);
    }


    @Override
    public PersonVO entityVO(Person entity) {
        PersonVO vo = BeanUtil.copyProperties(entity, PersonVO.class);

//        vo.setCreateUserName(userClient.userById(entity.getCreateUser()).getData().getName());
        vo.setCreateTime(DateUtil.formatDate(entity.getCreateTime()));

        // 处理封面图片
        Attach coverAttach = ossEndPoint.attachById(entity.getCoverFile()).getData();
        vo.setCoverSrc(ossEndPoint.fileLink(coverAttach.getName()).getData());
        if (entity.getFaceFile() != null) {
            Attach faceAttach = ossEndPoint.attachById(entity.getFaceFile()).getData();
            vo.setFaceSrc(ossEndPoint.fileLink(faceAttach.getName()).getData());
        }
        return vo;
    }
}
