package org.qwb.ai.oss.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import org.qwb.ai.common.api.R;
import org.qwb.ai.oss.config.AiCloudFile;
import org.qwb.ai.oss.config.MinioTemplate;
import org.qwb.ai.oss.config.OssFile;
import org.qwb.ai.oss.entity.Attach;
import org.qwb.ai.oss.repository.AttachRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(value = "/oss/endpoint")
@Data
public class OssEndpoint{

	@Resource
	private MinioTemplate minioTemplate;
	@Resource
	private AttachRepository attachRepository;

	private static final Logger logger = LoggerFactory.getLogger(OssEndpoint.class);

	@Value("${oss.endpoint}")
	private String endPoint;

	/**
	 * 创建存储桶
	 * 
	 * @param bucketName
	 * @return
	 */
	@SneakyThrows
	@PostMapping(value = "/make-bucket")
	public R makeBucket(@RequestParam String bucketName) {
		minioTemplate.makeBucket(bucketName);
		return R.success();
	}

	/**
	 * 获取文件信息
	 * 
	 * @param fileName
	 * @return
	 */
	@SneakyThrows
	@GetMapping(value = "/stat-file")
	public R<OssFile> statFile(@RequestParam String fileName) {
		return R.data(minioTemplate.statFile(fileName));
	}

	@GetMapping(value = "/stat-file-json")
	public String statFileJson(@RequestParam String fileName) {
		return JSONUtil.toJsonPrettyStr(minioTemplate.statFile(fileName));
	}

	/**
	 * 获取文件外链
	 *
	 * @param fileName 存储桶对象名称
	 * @return String
	 */
	@SneakyThrows
	@GetMapping("/file-link")
	public R<String> fileLink(@RequestParam String fileName) {
		return R.data(minioTemplate.fileLink(fileName));
	}

	
	@GetMapping("/attach-by-id")
	public R<Attach> attachById(@RequestParam Long attachId) {
		return R.data(attachRepository.findById(attachId).get());
	}

	
	@GetMapping("/attach-by-id-with-tenant")
	public R<Attach> attachByIdWithTenant(@RequestParam Long attachId, @RequestParam String tenantId) {
		return R.data(attachRepository.findById(attachId).get());
	}

	/**
	 * 上传文件
	 *
	 * @param file 文件
	 * @return ObjectStat
	 */
	@SneakyThrows
	@PostMapping("/put-file")
	public R<AiCloudFile> putFile(@RequestParam MultipartFile file) {
		AiCloudFile AiCloudFile = minioTemplate.putFile(file.getOriginalFilename(), file.getInputStream());
		return R.data(AiCloudFile);
	}

	@SneakyThrows
	@PostMapping("/put-file-ins")
	public R<AiCloudFile> putFileIns(@RequestParam String fileName, @RequestParam byte[] fileContent) {
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, IoUtil.toStream(fileContent));
		return R.data(AiCloudFile);
	}

	/**
	 * 上传文件
	 *
	 * @param fileName 存储桶对象名称
	 * @param file     文件
	 * @return ObjectStat
	 */
	@SneakyThrows
	@PostMapping("/put-file-by-name")
	public R<AiCloudFile> putFile(@RequestParam String fileName, @RequestParam MultipartFile file) {
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, file.getInputStream());
		return R.data(AiCloudFile);
	}

	/**
	 * 上传文件并保存至附件表
	 *
	 * @param file 文件
	 * @return ObjectStat
	 */
	@SneakyThrows
	@PostMapping("/put-file-attach")
	public R<AiCloudFile> putFileAttach(@RequestParam MultipartFile file) {
		String fileName = file.getOriginalFilename();
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, file.getInputStream());
		Long attachId = buildAttach(fileName, file.getSize(), AiCloudFile);
		AiCloudFile.setAttachId(attachId);
		return R.data(AiCloudFile);
	}

	@SneakyThrows
	@PostMapping("/put-attach")
	public R<Attach> putAttach(@RequestParam MultipartFile file) {
		String fileName = file.getOriginalFilename();
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, file.getInputStream());
		Attach attach = buildAttachEntity(fileName, file.getSize(), AiCloudFile);
		return R.data(attach);
	}

	@SneakyThrows
	@PostMapping("/put-attach-with-tenant")
	
	public R<Attach> putAttachWithTenant(MultipartFile file, String tenantId) {
		String fileName = file.getOriginalFilename();
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, file.getInputStream());
		Attach attach = buildAttachEntity(fileName, file.getSize(), AiCloudFile);
		return R.data(attach);
	}

	@SneakyThrows
	@PostMapping("/put-attach-ins")
	public R<Attach> putInsAttach(@RequestParam String fileName, @RequestParam InputStream ins) {
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, ins);
		Attach attach = buildAttachEntity(fileName, (long) ins.available(), AiCloudFile);
		return R.data(attach);
	}

	/**
	 * 上传文件并保存至附件表
	 *
	 * @param fileName 存储桶对象名称
	 * @param file     文件
	 * @return ObjectStat
	 */
	@SneakyThrows
	@PostMapping("/put-file-attach-by-name")
	public R<AiCloudFile> putFileAttach(@RequestParam String fileName, @RequestParam MultipartFile file) {
		AiCloudFile AiCloudFile = minioTemplate.putFile(fileName, file.getInputStream());
		Long attachId = buildAttach(fileName, file.getSize(), AiCloudFile);
		AiCloudFile.setAttachId(attachId);
		return R.data(AiCloudFile);
	}

	/**
	 * 删除文件
	 *
	 * @param fileName 存储桶对象名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/remove-file")
	public R removeFile(@RequestParam String fileName) {
		minioTemplate.removeFile(fileName);
		return R.success();
	}

	
	@PostMapping("/remove-file-by-id")
	public R removeFile(Long attachId) {
		Attach attach = attachRepository.findById(attachId).orElse(null);
		if(attach != null) {
			minioTemplate.removeFile(attach.getName());
			attachRepository.delete(attach);
		}
		return R.success();
	}

	
	@PostMapping("/remove-file-by-id-with-tenant")
	public R removeFileWithTenant(Long attachId, String tenantId) {
		return removeFile(attachId);
	}

	@SneakyThrows
	@GetMapping("/get-file-ins-by-domain-and-name")
	public ResponseEntity<InputStreamResource> getFileInsByDomainAndName(@RequestParam(required = false) String domain, @RequestParam(required = false) String name) {
		String bucketName = domain.substring(domain.lastIndexOf("/") + 1, domain.length());
		String objectName = name;
		InputStream ins = minioTemplate.getObject(bucketName, objectName);
		return ResponseEntity.ok(new InputStreamResource(ins));
	}


	private Attach buildAttachEntity(String fileName, Long fileSize, AiCloudFile AiCloudFile) {
		String fileExtension = FileUtil.getSuffix(fileName);
		Attach attach = new Attach();
		attach.setDomain(AiCloudFile.getDomain());
		attach.setLink(AiCloudFile.getLink());
		attach.setName(AiCloudFile.getName());
		attach.setOriginalName(AiCloudFile.getOriginalName());
		attach.setAttachSize(fileSize);
        attach.setExtension(fileExtension);
		attachRepository.save(attach);
		return attach;
	}

    
    /**
	 * 构建并保存附件表
	 *
	 * @param fileName  文件名
	 * @param fileSize  文件大小
	 * @param AiCloudFile 对象存储文件
	 * @return attachId
	 */
	private Long buildAttach(String fileName, Long fileSize, AiCloudFile AiCloudFile) {
		String fileExtension = FileUtil.getSuffix(fileName);
		Attach attach = new Attach();
		attach.setDomain(AiCloudFile.getDomain());
		attach.setLink(AiCloudFile.getLink());
		attach.setName(AiCloudFile.getName());
		attach.setOriginalName(AiCloudFile.getOriginalName());
		attach.setAttachSize(fileSize);
        attach.setExtension(fileExtension);
		attachRepository.save(attach);
		return attach.getId();
	}	

}