package org.qwb.ai.faceRecognition.service.impl;

import cn.hutool.core.io.FileUtil;
import org.qwb.ai.faceRecognition.entity.FaceStorage;
import org.qwb.ai.faceRecognition.repository.FaceStorageRepository;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.service.ILocalProcessService;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LocalProcessServiceImpl implements ILocalProcessService {
    @Resource
    private FaceStorageRepository faceStorageRepository;
    @Resource
    private FaceService insightFaceService;

    @Override
    public List<FaceStorage> isProcessed(String parentPath) {
        List<FaceStorage> processedFiles = new ArrayList<>();
        boolean b = FileUtil.isFile(parentPath);
        if (b){
            processedFiles.add(faceStorageRepository.findByParentPathAndOriginName(FileUtil.getParent(parentPath,1), FileUtil.getName(parentPath)));
        }else {
            processedFiles = faceStorageRepository.findByParentPath(parentPath);
        }
        return processedFiles;
    }

    @Override
    public void process(String path) {
        List<File> files = FileUtil.loopFiles(path);

    }

    @Override
    public Map<File, List<FaceRecVO>> process(List<File> files) {
        return null;
    }

    @Override
    public void buildFaceFeatureFolder(File file, FaceRecVO faceRecVO) {

    }
}
