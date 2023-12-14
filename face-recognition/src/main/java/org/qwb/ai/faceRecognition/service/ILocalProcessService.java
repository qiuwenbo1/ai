package org.qwb.ai.faceRecognition.service;

import org.qwb.ai.faceRecognition.entity.FaceStorage;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ILocalProcessService {
    /**
     * 查询本地图片/本地路径是否已经处理过
     * @param path 需要查询的路径/需要查询的路径
     * @return 已经处理过的文件
     */
    List<FaceStorage> isProcessed(String path);

//    @Async
//    void process(String path);

//    /**
//     * 对一批文件进行人脸检测
//     * @param files 文件集合
//     */
//    Map<File,List<FaceRecVO>> fileDetect(List<File> files);

//    /**
//     * 新构建本地人脸特征文件夹
//     * File 源文件
//     * List<FaceRecVO> 文件人脸特征集合
//     */
//    void buildFaceFeatureFolder(Map<File, List<FaceRecVO>> detectMap);
}
