package com.walyCommerce.walycommerce.dto;

import org.springframework.web.multipart.MultipartFile;

public class UriDto {

    private String uri;

    public UriDto(){}

    public UriDto(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
