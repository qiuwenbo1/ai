/*
 * @Author: liu lichao
 * @Date: 2021-03-22 09:39:19
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-03-22 09:39:42
 * @Description: file content
 */
package org.qwb.ai.faceRecognition.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.IoUtil;

public class InMemoryMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFileName;
    private final String contentType;
    private final byte[] payload;

    public InMemoryMultipartFile(File file) throws IOException {
        this.originalFileName = file.getName();
        this.payload = FileCopyUtils.copyToByteArray(file);
        this.name = "file";
        this.contentType = "application/octet-stream";
    }

    public InMemoryMultipartFile(String originalFileName, byte[] payload) {
        this.originalFileName = originalFileName;
        this.payload = payload;
        this.name = "file";
        this.contentType = "application/octet-stream";
    }

    public InMemoryMultipartFile(String originalFileName, InputStream ins) {
        this.originalFileName = originalFileName;
        this.payload = IoUtil.readBytes(ins);
        this.name = "file";
        this.contentType = "application/octet-stream";
    }

    public InMemoryMultipartFile(String name, String originalFileName, String contentType, byte[] payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null.");
        }
        this.name = name;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.payload = payload;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return payload.length == 0;
    }

    @Override
    public long getSize() {
        return payload.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return payload;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(payload);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(payload);
    }

}
