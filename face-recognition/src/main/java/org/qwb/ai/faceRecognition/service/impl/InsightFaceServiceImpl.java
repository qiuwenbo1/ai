package org.qwb.ai.faceRecognition.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageInfo;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.qwb.ai.faceRecognition.dto.FaceCompareDto;
import org.qwb.ai.faceRecognition.dto.FaceConstant;
import org.qwb.ai.faceRecognition.dto.FaceInfoDto;
import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.insight.InsightCompareEngine;
import org.qwb.ai.faceRecognition.insight.InsightEngineFactory;
import org.qwb.ai.faceRecognition.repository.FaceImageRepository;
import org.qwb.ai.faceRecognition.service.FaceService;
import org.qwb.ai.faceRecognition.utils.RedisUtil;
import org.qwb.ai.faceRecognition.vo.FaceRecVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Setter
public class InsightFaceServiceImpl implements FaceService {

    @Value("${app.insight.baseUri}")
    private String baseUri;
    private Logger logger = LoggerFactory.getLogger(InsightFaceServiceImpl.class);
    @Resource
    private FaceImageRepository faceImageRepository;
    @Resource
    private IOssEndPoint iOssEndPoint;
    @Resource
    private RedisUtil redisUtil;
    private ExecutorService compareExecutorService;

    @Value("${app.face.comparePoolSize}")
    private Integer comparePoolSize;
    //人脸比对引擎池
    private GenericObjectPool<InsightCompareEngine> faceEngineComparePool;
    @Value("${milvus.collection}")
    private String collection;


    @PostConstruct
    public void init() {
        try {
//初始化特征比较线程池
            logger.info("初始化Insight人脸比对线程池");
            GenericObjectPoolConfig comparePoolConfig = new GenericObjectPoolConfig();
            comparePoolConfig.setMaxIdle(comparePoolSize);
            comparePoolConfig.setMaxTotal(comparePoolSize);
            comparePoolConfig.setMinIdle(comparePoolSize);
            comparePoolConfig.setLifo(false);
            EngineConfiguration compareCfg = new EngineConfiguration();
            FunctionConfiguration compareFunctionCfg = new FunctionConfiguration();
            compareFunctionCfg.setSupportFaceRecognition(true);//开启人脸识别功能
            compareCfg.setFunctionConfiguration(compareFunctionCfg);
            compareCfg.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);//图片检测模式，如果是连续帧的视频流图片，那么改成VIDEO模式
            compareCfg.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);//人脸旋转角度
            faceEngineComparePool = new GenericObjectPool<InsightCompareEngine>(new InsightEngineFactory(), comparePoolConfig);//底层库算法对象池
            compareExecutorService = Executors.newFixedThreadPool(comparePoolSize);
            logger.info("初始化Insight人脸比对线程池成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @PostConstruct
    /**
     * 初始化人脸特征库到redis
     */
    @Override
    public void initFaceRepo() {
        Map<Object, Object> objs = redisUtil.hmget(FaceConstant.FACE_INSIGHT_FEATURE_MAP_KEY);
        Set<String> faceIdSet = Convert.toSet(String.class, new ArrayList<>(objs.keySet()));
        logger.info("初始化Insight Face人脸特征库");
        List<FaceImage> faceImages = faceImageRepository.findAll().stream().filter(m -> !faceIdSet.contains(String.valueOf(m.getId()))).collect(Collectors.toList());
        Map<String, Object> faceFeatureMap = new HashMap<>();
        try (ProgressBar pb = new ProgressBar("处理人脸图像特征", faceImages.size())) {
            List<List<Float>> vecList = new ArrayList<>();
            List<Long> faceIds = new ArrayList<>();
            for (FaceImage faceImage : faceImages) {
                List<FaceRecVO> result = null;
                try (InputStream faceImageIns = iOssEndPoint.getFileIns(faceImage.getFaceAttachName()).body().asInputStream()) {
                    String data = picsToBase64(faceImageIns);
                    if (data == null) continue;
                    result = detectPlusByBase64(List.of(data)).get(0);
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
                assert result != null;
                for (FaceRecVO faceRecVO : result) {
                    FaceInfoDto faceInfoDto = new FaceInfoDto(faceImage.getId(), faceRecVO.getVec());
                    faceFeatureMap.put(Convert.toStr(faceImage.getId()), faceInfoDto);
                    vecList.add(faceRecVO.getVec());
                    faceIds.add(faceImage.getId());
                }
                pb.step();
            }
            logger.info("Insight Face 初始化人脸库特征完毕，存储特征");
            boolean status = redisUtil.hmset(FaceConstant.FACE_INSIGHT_FEATURE_MAP_KEY, faceFeatureMap);
            logger.info("人脸库特征存储redis状态：【{}】", status);
        } catch (Exception e) {
            logger.error("{}--{}",InsightFaceServiceImpl.class, e.getMessage());
        }
    }

    private String picsToBase64(InputStream faceImageIns) throws IOException {
        BufferedImage bufferedImage;
        bufferedImage = ImageIO.read(faceImageIns);

        if (bufferedImage == null) {
            logger.error("minio不存在的图片");
            return null;
        }
        Image img = ImgUtil.scale(bufferedImage, 112, 112);
        return ImgUtil.toBase64DataUri(img, ImgUtil.IMAGE_TYPE_JPEG);
    }

    @Override
    public List<List<FaceRecVO>> detectPlusByLinks(List<String> urls) {
        JSONObject requestParams = getRequestParam();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("urls", List.of(urls));
        requestParams.set("images", dataMap);
        return faceInfoResultProcess(facePost(requestParams));
    }

    /**
     * 走base64的默认都是纯人脸特征
     * @param base64 人脸特征
     */
    @Override
    public List<List<FaceRecVO>> detectPlusByBase64(List<String> base64) {
        JSONObject requestParams = getRequestParam();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", base64);
        requestParams.set("images", dataMap);
        System.out.println(requestParams);
        return faceInfoResultProcess(facePost(requestParams));
    }

    @Override
    public List<FaceRecVO> detect(File image) {
        return detect(FileUtil.getInputStream(image));
    }

    @Override
    @SneakyThrows
    public List<FaceRecVO> detect(InputStream ins) {
        String data = ImgUtil.toBase64DataUri(ImgUtil.toImage(IoUtil.readBytes(ins)), ImgUtil.IMAGE_TYPE_JPEG);
        JSONObject requestParams = getRequestParam();
        // 数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", List.of(data));
        requestParams.set("images", dataMap);
        return faceInfoResultProcess(facePost(requestParams)).get(0);
    }

    public String facePost(JSONObject requestParams) {
        String url = baseUri + "/extract";
        return HttpUtil.post(url, requestParams.toString());
    }

    /**
     * 人脸特征信息结果处理
     *
     * @param faceInfoResult 算法请求后的人脸特征信息结果
     * @return 处理后的数据
     */
    public List<List<FaceRecVO>> faceInfoResultProcess(String faceInfoResult) {
        ArrayList<List<FaceRecVO>> result = new ArrayList<>();
        if (!JSONUtil.isTypeJSON(faceInfoResult)) return result;
        JSONObject resultObj = JSONUtil.parseObj(faceInfoResult);
        for (Object data : resultObj.getJSONArray("data")) {
            JSONObject parsed = JSONUtil.parseObj(data);
            JSONArray faces = parsed.getJSONArray("faces");
            List<FaceRecVO> faceInfos = new ArrayList<>();
            for (int i = 0; i < faces.size(); i++) {
                JSONObject faceObj = faces.getJSONObject(i);
                List<Integer> bbox = faceObj.getJSONArray("bbox").toList(Integer.class);
                FaceRecVO faceRecVO = new FaceRecVO(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
                faceRecVO.setVec(faceObj.containsKey("vec") ? faceObj.getJSONArray("vec").toList(Float.class) : null);
                faceInfos.add(faceRecVO);
            }
            result.add(faceInfos);
        }
        return result;
    }

    @Override
    public List<Float> extractFaceFeature(InputStream ins, FaceRecVO faceInfo) {
        // 裁剪人脸图片
        ByteArrayOutputStream os = new ByteArrayOutputStream(37628);
        long cutStart = DateUtil.current();
        ImgUtil.cut(
                ins,
                os,
                new Rectangle(faceInfo.getX(), faceInfo.getY(), faceInfo.getW(), faceInfo.getH())
        );
        logger.info("裁剪耗时：{}s", (DateUtil.current() - cutStart) / 1000.0);
        long base64Start = DateUtil.current();
        InputStream stream2 = new ByteArrayInputStream(os.toByteArray());
        Image img = ImgUtil.scale(ImgUtil.toImage(IoUtil.readBytes(stream2)), 112, 112);
        String data = ImgUtil.toBase64DataUri(img, ImgUtil.IMAGE_TYPE_JPEG);
        logger.info("转base64：{}s", (DateUtil.current() - base64Start) / 1000.0);
        JSONObject requestParams = getRequestParam();
        requestParams.set("embed_only", true);
        requestParams.set("extract_embedding", true);

        // 数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", List.of(data));
        requestParams.set("images", dataMap);

        String url = baseUri + "/extract";
        long timeStart = DateUtil.current();
        String result = HttpUtil.post(url, requestParams.toString());
        logger.info("调用耗时：{}s", (DateUtil.current() - timeStart) / 1000.0);

        if (JSONUtil.isTypeJSON(result)) {
            return JSONUtil.parseObj(result).getJSONArray("data").getJSONObject(0).getJSONArray("vec").toList(Float.class);
        }
        return null;
    }

    /**
     * 人脸识别
     *
     * @param faceFeature 人脸特征
     * @param passRate    通过率
     * @return 相似的人脸
     */
    @Override
    public List<FaceCompareDto> faceRecognition(List<Float> faceFeature, float passRate) {
        Map<Object, Object> objs = redisUtil.hmget(FaceConstant.FACE_INSIGHT_FEATURE_MAP_KEY);

        List<FaceInfoDto> faceInfos = Convert.toList(FaceInfoDto.class, new ArrayList<>(objs.values()));
        return compareDtoList(faceInfos, passRate, faceFeature);
    }

    /**
     * redis 进行比较人脸
     *
     * @param faceInfos   redis存储的人脸特征
     * @param passRate    通过率
     * @param faceFeature 要校验的人脸
     * @return 最相似的人脸
     */
    private List<FaceCompareDto> compareDtoList(List<FaceInfoDto> faceInfos, float passRate, List<Float> faceFeature) {
        List<FaceCompareDto> result = Lists.newLinkedList();//识别到的人脸列表
        // 1000个人脸一组
        List<List<FaceInfoDto>> faceInfoPartList = Lists.partition(faceInfos, 100);
        CompletionService<List<FaceCompareDto>> completionService = new ExecutorCompletionService<>(compareExecutorService);
        for (List<FaceInfoDto> part : faceInfoPartList) {
            completionService.submit(new CompareFaceTask(part, faceFeature, passRate));
        }
        for (int i = 0; i < faceInfoPartList.size(); i++) {
            List<FaceCompareDto> FaceCompareDtoList = null;
            try {
                FaceCompareDtoList = completionService.take().get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
            }
            if (CollectionUtil.isNotEmpty(faceInfos)) {
                assert FaceCompareDtoList != null;
                result.addAll(FaceCompareDtoList);
            }
        }
        result.sort((h1, h2) -> h2.getSimilar().compareTo(h1.getSimilar()));//从大到小排序
        return result;
    }

//    /**
//     * milvus 进行校验检索人脸
//     * @param faceFeature
//     * @param passRate
//     * @return
//     */
//    public List<FaceCompareDto> faceRecognitions(List<Float> faceFeature, float passRate){
//        Map<Object, Object> objs = redisUtil.hmget(FaceConstant.FACE_INSIGHT_FEATURE_MAP_KEY);
//        SearchParam searchParam = SearchParam.newBuilder()
//                .withCollectionName(collection)
//                .withPartitionNames(List.of("ylj"))
//                .withMetricType(MetricType.L2)
//                .withOutFields(List.of("face_id"))
//                .withTopK(10)
//                .withVectors(List.of(faceFeature))
//                .withVectorFieldName("face_embedding")
////                .withParams(SEARCH_PARAM)
//                .build();
//        io.milvus.param.R<SearchResults> respSearch = milvusServiceClient.search(searchParam);
//        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
//        List<SearchResultsWrapper.IDScore> idScores = wrapperSearch.getIDScore(0);
//        List<?> imageIds = wrapperSearch.getFieldData("face_id", 0);
//        List<Object> faceInfoDto = new ArrayList<>();
//        for (int i = 0; i < idScores.size(); i++) {
//            Long faceId = Convert.toLong(imageIds.get(i));
//            Object o = objs.get(faceId.toString());
//            faceInfoDto.add(o);
////            FaceImage faceImage = faceImageRepository.findById(faceId).orElse(null);
////            assert faceImage != null;
////            Person person = personRepository.findById(faceImage.getPerson()).orElse(null);
////            assert person != null;
////            logger.info("人物：【{}】，相似度：【{}】",person.getName(),score);
//        }
//        List<FaceInfoDto> faceInfoDtos = Convert.toList(FaceInfoDto.class, new ArrayList<>(faceInfoDto));
//        return compareDtoList(faceInfoDtos,passRate,faceFeature);
//    }

    @Override
    public List<FaceCompareDto> faceRecognition(InputStream ins, float passRate) {
        return null;
    }


    private class CompareFaceTask implements Callable<List<FaceCompareDto>> {

        private List<FaceInfoDto> faceInfoDtoList;
        private List<Float> targetFaceFeature;
        private float passRate;


        public CompareFaceTask(List<FaceInfoDto> faceInfoDtoList, List<Float> targetFaceFeature, float passRate) {
            this.faceInfoDtoList = faceInfoDtoList;
            this.targetFaceFeature = targetFaceFeature;
            this.passRate = passRate;
        }

        @Override
        public List<FaceCompareDto> call() {
            InsightCompareEngine faceEngine = null;
            List<FaceCompareDto> resultUserInfoList = Lists.newLinkedList();//识别到的人脸列表
            try {
                faceEngine = faceEngineComparePool.borrowObject();
                for (FaceInfoDto userInfo : faceInfoDtoList) {
                    double similar = faceEngine.compare(userInfo.getInsightFeature(), targetFaceFeature);
                    if (similar > passRate) {//相似值大于配置预期，加入到识别到人脸的列表
                        FaceCompareDto info = new FaceCompareDto();
                        info.setId(userInfo.getId());
                        info.setSimilar(Convert.toFloat(similar));
                        resultUserInfoList.add(info);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                if (faceEngine != null) {
                    faceEngineComparePool.returnObject(faceEngine);
                }
            }

            return resultUserInfoList;
        }

    }


    private JSONObject getRequestParam() {
        JSONObject requestParams = JSONUtil.createObj();
        requestParams.set("max_size", Arrays.asList(640, 640));
        requestParams.set("threshold", 0.6);
        requestParams.set("embed_only", false);
        requestParams.set("return_landmarks", false);
        requestParams.set("extract_embedding", true);
        requestParams.set("extract_ga", false);
        requestParams.set("detect_masks", false);
        requestParams.set("limit_faces", 0);
        requestParams.set("min_face_size", 0);
        requestParams.set("verbose_timings", true);
        requestParams.set("api_ver", "2");
        requestParams.set("msgpack", false);
        return requestParams;
    }

    @Override
    public List<FaceCompareDto> faceRecognitions(List<Float> faceFeature, float passRate) {
        return null;
    }

    @Override
    public List<FaceRecVO> detect(InputStream ins, String imgType) {
        return null;
    }

    @Override
    public List<FaceRecVO> detect(ImageInfo imageInfo) {
        return null;
    }

    @Override
    public byte[] extractFaceFeature(ImageInfo imageInfo, FaceRecVO faceInfo) {
        return new byte[0];
    }

    @Override
    public List<FaceCompareDto> faceRecognition(byte[] faceFeature, float passRate) {
        return null;
    }
}
