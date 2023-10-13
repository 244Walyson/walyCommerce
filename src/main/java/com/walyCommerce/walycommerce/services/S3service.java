package com.walyCommerce.walycommerce.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

@Service
public class S3service {

    private static Logger Log = LoggerFactory.getLogger(S3service.class);

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucket}")
    private String bucketName;

    public URL UploadFile(MultipartFile file){
        try{
            String originalName = file.getOriginalFilename();
            Log.info((originalName));
            String extension = FilenameUtils.getExtension(originalName);
            String fileName = Instant.now().getEpochSecond() + "." + extension;
            Log.info("ex " + extension + "fl "+ fileName);
            InputStream is = file.getInputStream();
            String contentType = file.getContentType();
            Log.info(contentType);
            return uploadFile(is, fileName, contentType);
        }
        catch (IOException e){
            throw new IllegalArgumentException("falha no tratamento de arquivo");
        }
    }

    private URL uploadFile(InputStream is, String fileName, String contentType) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        meta.setContentLength(is.available());
        s3client.putObject(bucketName, fileName, is, meta);
        return s3client.getUrl(bucketName, fileName);
    }
}
