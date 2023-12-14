package org.qwb.ai.faceRecognition.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.qwb.ai.common.api.R;
import org.qwb.ai.common.dto.UploadFileDto;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Log4j2
@Service
public class FaceUploadOss {
    @Resource
    private IOssEndPoint iOssEndPoint;


    public Attach faceUpload(File file) {
        Attach attach = null;
        String suffix = FileUtil.getSuffix(file);
        if (!(suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpeg")))
            return attach;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            R<Attach> r = iOssEndPoint.putFileIns(new UploadFileDto(file.getName(), inputStream.readAllBytes()));
            attach = r.getData();
        } catch (IOException e) {
            log.error("【{}】,【{}】，【{}】", FaceUploadOss.log, e.getMessage(), file.getAbsolutePath());
        }
        return attach;
    }

}
