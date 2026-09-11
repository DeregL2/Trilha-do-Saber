package com.tcc.trilha_do_saber;

import com.tcc.trilha_do_saber.service.GabaritoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("import")
@Configuration
public class TesteGabaritoConfig {

    @Bean
    @Order(2)
    public CommandLineRunner testarGabarito(GabaritoService gabaritoService) {
        return args -> {
            int total = gabaritoService.gabaritoMontado("src/main/resources/gabarito/s2_gabarito (1).pdf");
            System.out.println("Alternativas corrigidas: " + total);
        };
    }
}