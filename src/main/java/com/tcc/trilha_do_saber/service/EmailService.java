package com.tcc.trilha_do_saber.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String remetente;

    public EmailService(@Value("${resend.api.key}") String apiKey,
                        @Value("${mail.remetente:Trilha do Saber <onboarding@resend.dev>}") String remetente){
        this.remetente = remetente;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public boolean enviarCodigoVerificacao(String nomeDestinatario, String emailDestinatario, String codigo){
        String html = """
                <div style="font-family: Arial, sans-serif; color: #202124;">
                    <p>Olá, %s!</p>
                    <p>Use o código abaixo para concluir seu login na plataforma Trilha do Saber:</p>
                    <p style="font-size: 28px; font-weight: bold; letter-spacing: 6px;">%s</p>
                    <p>Esse código é válido por 10 minutos. Se você não tentou entrar na plataforma, ignore este e-mail.</p>
                </div>
                """.formatted(nomeDestinatario, codigo);

        try {
            restClient.post()
                    .uri("/emails")
                    .body(Map.of(
                            "from", remetente,
                            "to", emailDestinatario,
                            "subject", "Seu código de verificação - Trilha do Saber",
                            "html", html
                    ))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            System.out.println("[Falha ao enviar e-mail: " + e.getMessage() + "] Código de verificação para " + emailDestinatario + ": " + codigo);
            return false;
        }
    }
}