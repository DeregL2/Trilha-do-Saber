package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.service.LogAuditoriaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Marco Civil da Internet (Lei 12.965/14). Apaga os dados após 1 ano, durante a noite.
@Component
public class ExpurgoLogAuditoriaTask {

    private final LogAuditoriaService logAuditoriaService;

    public ExpurgoLogAuditoriaTask(LogAuditoriaService logAuditoriaService){
        this.logAuditoriaService = logAuditoriaService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void executar(){
        int removidos = logAuditoriaService.expurgarLogsAntigos();
        if (removidos > 0) {
            System.out.println("Expurgo de logs: " + removidos + " registros com mais de 1 ano removidos.");
        }
    }
}