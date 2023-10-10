package com.walyCommerce.walycommerce.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileDTO {

    private MultipartFile file;

    public FileDTO(){}

    public FileDTO(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
