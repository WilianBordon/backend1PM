package com.drones.backend1PM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Backend1PmApplication {

	public static void main(String[] args) {
		SpringApplication.run(Backend1PmApplication.class, args);
	}

}
