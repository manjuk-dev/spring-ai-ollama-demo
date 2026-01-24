package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // enabling schedule clean up after 7 days for h2 file
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
