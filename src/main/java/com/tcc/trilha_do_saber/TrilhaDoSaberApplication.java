package com.tcc.trilha_do_saber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TrilhaDoSaberApplication {

	public static final String FUSO_HORARIO = "America/Sao_Paulo";

	public static void main(String[] args) {
		configurarFusoHorario();
		SpringApplication.run(TrilhaDoSaberApplication.class, args);
	}

	static void configurarFusoHorario() {
		TimeZone.setDefault(TimeZone.getTimeZone(FUSO_HORARIO));
	}

}