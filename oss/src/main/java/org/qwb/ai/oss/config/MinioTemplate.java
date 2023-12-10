package org.qwb.ai.oss.config;

import java.io.InputStream;
import java.util.*;

import io.minio.*;
import io.minio.messages.DeleteObject;
import org.qwb.ai.common.entity.Attach;
import org.qwb.ai.common.utils.StringPool;
import org.springframework.web.multipart.MultipartFile;

import io.minio.messages.Bucket;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class MinioTemplate implements OssTemplate {

    /**
     * MinIO客户端
     */
    private final MinioClient client;

    /**
     * 存储桶命名规则
     */
    private final OssRule ossRule;

    /**
     * 配置类
     */
    private final OssProperties ossProperties;

    @SneakyThrows
    public InputStream getObject(String objectName) {
        return client.getObject(GetObjectArgs.builder().object(objectName).bucket(ossProperties.getBucketName()).build());
    }

    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        return client.getObject(GetObjectArgs.builder().object(objectName).bucket(bucketName).build());
    }

    @Override
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) return;
        client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        client.setBucketPolicy(
                SetBucketPolicyArgs
                        .builder()
                        .bucket(bucketName)
                        .config(getPolicyType(bucketName, PolicyType.READ_WRITE))
                        .build());
    }

    @SneakyThrows
    public Bucket getBucket() {
        return getBucket(getBucketName());
    }

    @SneakyThrows
    public Bucket getBucket(String bucketName) {
        Optional<Bucket> bucketOptional = client.listBuckets().stream().filter(bucket -> bucket.name().equals(getBucketName(bucketName))).findFirst();
        return bucketOptional.orElse(null);
    }

    @SneakyThrows
    public List<Bucket> listBuckets() {
        return client.listBuckets();
    }

    @Override
    @SneakyThrows
    public void removeBucket(String bucketName) {
        client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    @Override
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @Override
    @SneakyThrows
    public void copyFile(String sourceBucketName, String sourceFileName, String bucketName) {
        client.copyObject(CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(sourceFileName)
                .source(
                        CopySource.builder()
                                .bucket(sourceBucketName)
                                .object(sourceFileName)
                                .build())
                .build());
    }

    @Override
    @SneakyThrows
    public void copyFile(String sourceBucketName, String sourceFileName, String bucketName, String fileName) {
        client.copyObject(CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .source(
                        CopySource.builder()
                                .bucket(sourceBucketName)
                                .object(sourceFileName)
                                .build())
                .build());
    }

    @Override
    @SneakyThrows
    public OssFile statFile(String fileName) {
        return statFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public OssFile statFile(String bucketName, String fileName) {
        StatObjectResponse statObject = client.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setLink(fileLink(ossFile.getName()));
        ossFile.setHash(String.valueOf(statObject.hashCode()));
        ossFile.setSize(statObject.size());
        ossFile.setPutTime(Date.from(statObject.lastModified().toInstant()));
        ossFile.setContentType(statObject.contentType());
        return ossFile;
    }

    @SneakyThrows
    public OssFile statFile2(String bucketName, String fileName) {
        StatObjectResponse stat = client.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName).build());
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setHash(String.valueOf(stat.hashCode()));
        ossFile.setSize(stat.size());
        ossFile.setPutTime(Date.from(stat.lastModified().toInstant()));
        ossFile.setContentType(stat.contentType());
        return ossFile;
    }

    @Override
    public String filePath(String fileName) {
        return getBucketName().concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    public String filePath(String bucketName, String fileName) {
        return getBucketName(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    @SneakyThrows
    public String fileLink(String fileName) {
        return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName()).concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    @SneakyThrows
    public String fileLink(String bucketName, String fileName) {
        return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName(bucketName)).concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    @SneakyThrows
    public Attach putFile(MultipartFile file) {
        return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
    }

    @Override
    @SneakyThrows
    public Attach putFile(String fileName, MultipartFile file) {
        return putFile(ossProperties.getBucketName(), fileName, file);
    }

    @Override
    @SneakyThrows
    public Attach putFile(String bucketName, String fileName, MultipartFile file) {
        return putFile(bucketName, file.getOriginalFilename(), file.getInputStream());
    }

    @Override
    @SneakyThrows
    public Attach putFile(String fileName, InputStream stream) {
        return putFile(ossProperties.getBucketName(), fileName, stream);
    }

    @Override
    @SneakyThrows
    public Attach putFile(String bucketName, String fileName, InputStream stream) {
        return putFile(bucketName, fileName, stream, "application/octet-stream");
    }

    @SneakyThrows
    public Attach putFile(String bucketName, String fileName, InputStream stream, String contentType) {
        makeBucket(bucketName);
        String originalName = fileName;
        fileName = getFileName(fileName);
        ObjectWriteResponse response = client.putObject(PutObjectArgs.builder().bucket(bucketName)
                .object(fileName)
                .userMetadata(Map.of("originName",originalName)).stream(
                        stream, -1, 10485760)
                .contentType(contentType)
                .build());
        Attach file = new Attach();
        file.setOriginalName(originalName);
        file.setName(fileName);
        file.setEtag(response.etag());
        file.setDomain(getOssHost(bucketName));
        file.setLink(fileLink(bucketName, fileName));
        return file;
    }

    @Override
    @SneakyThrows
    public void removeFile(String fileName) {
        removeFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    @SneakyThrows
    public void removeFile(String bucketName, String fileName) {
        client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

    @Override
    @SneakyThrows
    public void removeFiles(List<String> fileNames) {
        removeFiles(ossProperties.getBucketName(), fileNames);

    }

    @Override
    @SneakyThrows
    public void removeFiles(String bucketName, List<String> fileNames) {
        List<DeleteObject> list = new ArrayList<>();
        for (String fileName : fileNames) {
            list.add(new DeleteObject(fileName));
        }
        client.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(list).build());
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @return String
     */
    private String getBucketName() {
        return getBucketName(ossProperties.getBucketName());
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @param bucketName 存储桶名称
     * @return String
     */
    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }

    /**
     * 根据规则生成文件名称规则
     *
     * @param originalFilename 原始文件名
     * @return string
     */
    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }

    /**
     * 获取存储桶策略
     *
     * @param policyType 策略枚举
     * @return String
     */
    public String getPolicyType(PolicyType policyType) {
        return getPolicyType(getBucketName(), policyType);
    }

    /**
     * 获取存储桶策略
     *
     * @param bucketName 存储桶名称
     * @param policyType 策略枚举
     * @return String
     */
    public static String getPolicyType(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");

        switch (policyType) {
            case WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            case READ_WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucket\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            default:
                builder.append("                \"s3:GetBucketLocation\"\n");
                break;
        }

        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n");
        builder.append("        },\n");
        if (PolicyType.READ.equals(policyType)) {
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Deny\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n");
            builder.append("        },\n");

        }
        builder.append("        {\n");
        builder.append("            \"Action\": ");

        switch (policyType) {
            case WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            case READ_WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:GetObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
                break;
        }

        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * 获取域名
     *
     * @param bucketName 存储桶名称
     * @return String
     */
    public String getOssHost(String bucketName) {
        return ossProperties.getEndpoint() + StringPool.SLASH + getBucketName(bucketName);
    }

    /**
     * 获取域名
     *
     * @return String
     */
    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

}
