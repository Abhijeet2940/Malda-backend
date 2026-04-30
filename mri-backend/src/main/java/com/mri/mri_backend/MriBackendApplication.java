package com.mri.mri_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MriBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MriBackendApplication.class, args);
	}

}
