package com.tcc.trilha_do_saber.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailService(JavaMailSender mailSender,
                        @Value("${mail.remetente:Trilha do Saber <trilhadosaberpfc@gmail.com>}") String remetente){
        this.mailSender = mailSender;
        this.remetente = remetente;
    }
    public boolean enviarCodigoVerificacao(String nomeDestinatario, String emailDestinatario, String codigo){
        String assunto = "Seu código de verificação - Trilha do Saber";
        String html = """
                <div style="font-family: Arial, sans-serif; color: #202124;">
                    <p>Olá, %s!</p>
                    <p>Use o código abaixo para concluir seu login na plataforma Trilha do Saber:</p>
                    <p style="font-size: 28px; font-weight: bold; letter-spacing: 6px;">%s</p>
                    <p>Esse código é válido por 10 minutos. Se você não tentou entrar na plataforma, ignore este e-mail.</p>
                </div>
                """.formatted(nomeDestinatario, codigo);

        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(emailDestinatario);
            helper.setSubject(assunto);
            helper.setText(html, true);

            mailSender.send(mensagem);
            return true;
        } catch (Exception e) {
            System.out.println("[Falha ao enviar e-mail: " + e.getMessage() + "] Código de verificação para " + emailDestinatario + ": " + codigo);
            return false;
        }
    }
}