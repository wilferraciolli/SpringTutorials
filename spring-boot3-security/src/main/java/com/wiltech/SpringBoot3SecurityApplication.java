package com.wiltech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class SpringBoot3SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot3SecurityApplication.class, args);
	}

}
