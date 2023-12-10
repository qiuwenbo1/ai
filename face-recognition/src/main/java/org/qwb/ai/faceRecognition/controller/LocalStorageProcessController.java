package org.qwb.ai.faceRecognition.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.qwb.ai.common.api.R;
import org.qwb.ai.common.dto.UploadFileDto;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.faceRecognition.entity.FaceStorage;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.service.ILocalProcessService;
import org.qwb.ai.faceRecognition.vo.FaceRecResultVO;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(value = "/local-process")
public class LocalStorageProcessController {

    @Resource
    private FaceService insightFaceService;
    @Resource
    private IOssEndPoint iOssEndPoint;
    @Resource
    private ILocalProcessService localProcessService;

    @RequestMapping(value = "/infer2")
    public R infer(@RequestParam String sourPath,@RequestParam String outPath) {
        List<File> files = FileUtil.loopFiles(sourPath);
        Map<String, List<File>> collect = files.stream().collect(Collectors.groupingBy(File::getParent));
        for (Map.Entry<String, List<File>> entry : collect.entrySet()) {
            String key = entry.getKey();
            List<File> fileList = entry.getValue();
            List<FaceStorage> storages = localProcessService.isProcessed(key);
            if (fileList.size() == storages.size()) continue;
            localProcessService.process(fileList);
        }
        for (File file : files) {
            String suffix = FileUtil.getSuffix(file);
            if (!(suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpeg")))
                continue;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                R<Attach> objectR = iOssEndPoint.putFileIns(new UploadFileDto(file.getName(), inputStream.readAllBytes()));
                Attach data = objectR.getData();
                String link = data.getLink();
                List<FaceRecVO> faceRecVOS = insightFaceService.detectPlusByLinks(Collections.singletonList(link));
                if (faceRecVOS.isEmpty()) continue;
            } catch (IOException e) {
                log.error("文件上传失败。原因：【{}】，文件全路径：【{}】",e.getMessage(),file.getAbsolutePath());
            }
        }
        return R.success();
    }
}
