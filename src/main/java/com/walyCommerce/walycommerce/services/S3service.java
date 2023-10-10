package com.walyCommerce.walycommerce.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3service {

    private static Logger Log = LoggerFactory.getLogger(S3service.class);

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucket}")
    private String bucketName;

    public void UploadFile(String localFilePath){
        try{
            File file = new File(localFilePath);
            Log.info("to aqui: " + localFilePath + "bucket "+ bucketName);
            s3client.putObject(new PutObjectRequest(bucketName, "testtt.jpg", file));
        }
        catch(AmazonServiceException e){
            Log.info(e.getMessage() + " erro ");
        }
        catch (AmazonClientException e ){
            Log.info(e.getMessage()+ "errror");
        }
    }
}
