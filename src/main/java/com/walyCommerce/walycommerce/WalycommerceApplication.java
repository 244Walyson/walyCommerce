package com.walyCommerce.walycommerce;

import com.walyCommerce.walycommerce.services.S3service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class WalycommerceApplication  implements CommandLineRunner{


	@Autowired
	private S3service s3service;
	public static void main(String[] args) {
		SpringApplication.run(WalycommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		s3service.UploadFile("C:/Users/walys/vsprojects/figma/images engenharia de requisitos/Sem título.png");
	}
}
