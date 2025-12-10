package org.example.labms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LabmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabmsApplication.class, args);
	}

}