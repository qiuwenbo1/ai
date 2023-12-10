package org.qwb.ai.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadFileDto implements Serializable {
    private String fileName;
    private byte[] fileContent;
    public UploadFileDto(){
    }

    public UploadFileDto(String fileName,byte[] fileContent){
        this.fileName = fileName;
        this.fileContent = fileContent;
    }
}
