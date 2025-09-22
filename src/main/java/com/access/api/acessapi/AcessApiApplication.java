package com.access.api.acessapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.access.api.acessapi")
public class AcessApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcessApiApplication.class, args);
	}

}
