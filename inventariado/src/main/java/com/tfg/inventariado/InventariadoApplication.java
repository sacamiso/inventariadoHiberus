package com.tfg.inventariado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InventariadoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventariadoApplication.class, args);
	}

}
