package org.qwb.ai.faceRecognition;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.tongfei.progressbar.ProgressBar;
import org.junit.jupiter.api.Test;
import org.qwb.ai.common.dto.UploadFileDto;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.common.repository.AttachRepository;
import org.qwb.ai.faceRecognition.feign.IOssEndPoint;
import org.qwb.ai.faceRecognition.service.impl.FaceUploadOss;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class FaceRecognitionApplicationTests {
    @Resource
    private IOssEndPoint iOssEndPoint;
    @Resource
    private AttachRepository attachRepository;
    @Resource
    private FaceUploadOss faceUploadOss;

    @Test
    void contextLoads() {
        JSONObject requestParams = JSONUtil.createObj();
        requestParams.set("max_size", Arrays.asList(640, 640));
        requestParams.set("threshold", 0.6);
        requestParams.set("embed_only", false);
        requestParams.set("extract_embedding", true);
        requestParams.set("return_face_data", false);
        requestParams.set("return_landmarks", false);
        requestParams.set("extract_ga", false);
        requestParams.set("detect_masks", false);
        requestParams.set("limit_faces", 0);
        requestParams.set("min_face_size", 0);
        requestParams.set("verbose_timings", true);
        requestParams.set("api_ver", "2");
        requestParams.set("msgpack", false);

//        String pic="D:\\file\\个人\\照片\\02.个体资料\\一帆\\微信图片_20231103001523.jpg";
//        BufferedImage bufferedImage = ImageIO.read(new File(pic));
        String baseUri = "http://192.168.1.3:18081";
//        Image img = ImgUtil.scale(bufferedImage, 112, 112);
//        String data = ImgUtil.toBase64DataUri(img, ImgUtil.IMAGE_TYPE_JPEG);
        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("data", List.of(data));
        dataMap.put("urls",List.of("http://192.168.1.3:9000/ai-cloud-001/upload/2023-12-08/5f5bfdf9-9315-417e-9658-d63266dd081d.jpg"));
        requestParams.set("images", dataMap);

        String url = baseUri + "/extract";
        String result = HttpUtil.post(url, requestParams.toString());
        System.out.println(result);
    }

    @Test
    void setDate(){
        List<Attach> all = attachRepository.findAll();
        for (Attach attach : all) {
            if (attach.getAttachSize() != 0L) continue;
            Long size = iOssEndPoint.statFile(attach.getName()).getData().getLong("size");
            System.out.println(size);
            attach.setAttachSize(size);
            attachRepository.save(attach);
        }
    }
    @Test
    void tt(){
        File file = new File("D:\\file\\个人\\照片\\02.个体资料\\一帆\\微信图片_20231103001037.jpg");
        System.out.println(file.getParent());
    }

    @Test
    void test1() {
        String s = DigestUtil.md5Hex(new File("D:\\file\\个人\\照片\\02.个体资料\\一帆\\微信图片_20231103001037.jpg"));
        String s1 = DigestUtil.md5Hex(new File("D:\\file\\个人\\照片\\02.个体资料\\一帆\\Snipaste_2023-11-06_18-56-04.png"));
        List<File> files = FileUtil.loopFiles("D:\\file\\个人\\照片");
        try (ProgressBar pb = new ProgressBar("上传图片", files.size())) {
            for (File file : files) {
                pb.step();
                faceUploadOss.faceUpload(file);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
