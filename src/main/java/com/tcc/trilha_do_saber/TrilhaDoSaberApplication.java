package com.tcc.trilha_do_saber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrilhaDoSaberApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrilhaDoSaberApplication.class, args);
	}

}