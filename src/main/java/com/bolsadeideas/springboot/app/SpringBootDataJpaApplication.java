package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringBootDataJpaApplication implements CommandLineRunner {
	
	@Autowired
	IUploadFileService uploadFileService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataJpaApplication.class, args);
	}

	//PARA QUE SE GENERE AUTOMATICAMENTE EL DIRECTORIO UPLOADS
	@Override
	public void run(String... args) throws Exception {
		uploadFileService.deleteAll(); //OJO BORRA TODO LO DEL DIRECTORIO
		uploadFileService.init();
		
	}

}
