package com.tcc.trilha_do_saber;

import com.tcc.trilha_do_saber.service.ImportadorProvaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesteImportacaoConfig {

    @Bean
    public CommandLineRunner testarImportacao(ImportadorProvaService importadorProvaService) {
        return args -> {
            int total = importadorProvaService.provaMontada(
                    "src/main/resources/provas/s1_prova.pdf",
                    null, // Tema: null por enquanto, só pra validar a persistência
                    2023, // ano
                    "Sistemas de Informação" // curso
            );
            System.out.println("Questões importadas: " + total);
        };
    }
}