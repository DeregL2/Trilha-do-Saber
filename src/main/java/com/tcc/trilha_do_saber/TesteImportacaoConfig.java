package com.tcc.trilha_do_saber;

import com.tcc.trilha_do_saber.service.ImportadorProvaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("import")
@Configuration
public class TesteImportacaoConfig {

    @Bean
    @Order(1)
    public CommandLineRunner testarImportacao(ImportadorProvaService importadorProvaService) {
        return args -> {
            int total = importadorProvaService.provaMontada(
                    "src/main/resources/provas/s1_prova.pdf",
                    null,
                    2023,
                    "Sistemas de Informação"
            );
            System.out.println("Questões importadas: " + total);
        };
    }
}