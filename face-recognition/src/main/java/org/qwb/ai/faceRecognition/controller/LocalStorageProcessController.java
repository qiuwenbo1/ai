package org.qwb.ai.faceRecognition.controller;

import lombok.extern.log4j.Log4j2;
import org.qwb.ai.common.api.R;
import org.qwb.ai.faceRecognition.service.ILocalProcessService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Log4j2
@RestController
@RequestMapping(value = "/local-process")
public class LocalStorageProcessController {
    @Resource
    private ILocalProcessService localProcessService;

    @RequestMapping(value = "/begin")
    public R infer(@RequestParam String sourPath,@RequestParam String outPath) {
        localProcessService.process(sourPath,outPath);
        return R.success();
    }
}
