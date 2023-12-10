package org.qwb.ai.faceRecognition.feign;

import cn.hutool.json.JSONObject;
import feign.Response;
import org.qwb.ai.common.api.R;
import org.qwb.ai.common.constant.AppConstant;
import org.qwb.ai.common.dto.UploadFileDto;
import org.qwb.ai.common.entity.Attach;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author demoQ
 * @date 2021/12/9 10:06
 */
@FeignClient(
        name = AppConstant.APPLICATION_OSS + "${router.name}",
        contextId = "IOssEndPoint"
)
public interface IOssEndPoint {

    String path = "/oss/endpoint";
    @PostMapping(value = path + "/make-bucket")
    R makeBucket(@RequestParam String bucketName);

    @GetMapping(value = path + "/stat-file")
    R<JSONObject> statFile(@RequestParam String fileName);

    @GetMapping(value = path + "/stat-file-json")
    String statFileJson(@RequestParam String fileName);

    @GetMapping(path + "/file-link")
    R<String> fileLink(@RequestParam String fileName);

    @GetMapping(path + "/attach-by-id")
    R<Attach> attachById(@RequestParam Long attachId);

    @GetMapping(path + "/attach-by-id-with-tenant")
    R<JSONObject> attachByIdWithTenant(@RequestParam Long attachId, @RequestParam String tenantId);

    @PostMapping(path + "/put-file-ins")
    R<Attach> putFileIns(@RequestBody UploadFileDto dto);

    @PostMapping(path + "/put-file-by-name")
    R<JSONObject> putFile(@RequestParam String fileName, @RequestParam MultipartFile file);

    @PostMapping(value = path + "/put-attach", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<Attach> putAttach(@RequestPart MultipartFile file);

    @PostMapping(path + "/put-attach-ins")
    R<JSONObject> putInsAttach(@RequestParam String fileName, @RequestParam InputStream ins);

    @PostMapping(path + "/remove-file")
    R removeFile(@RequestParam String fileName);

    @PostMapping(path + "/remove-file-by-id")
    R removeFile(@RequestParam Long attachId);

    @PostMapping(path + "/remove-file-by-id-with-tenant")
    R removeFileWithTenant(@RequestParam Long attachId, @RequestParam String tenantId);

//    @PostMapping(path + "/get-file-ins")
//    ResponseEntity<InputStreamResource> getFileIns(@RequestParam String fileName);

    @GetMapping(path + "/get-file-ins-by-domain-and-name")
    ResponseEntity<InputStreamResource> getFileInsByDomainAndName(@RequestParam(required = false) String domain, @RequestParam(required = false) String name);

    @PostMapping(path + "/get-file-ins")
    Response getFileIns(@RequestParam String fileName);
//
//    @PostMapping(path + "/get-file-ins-with-tenant")
//    Response getFileInsWithTenant(@RequestParam String fileName, @RequestParam String tenantId);

}
