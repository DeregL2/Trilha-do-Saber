package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.dto.VerificacaoSessaoDTO;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.service.EmailService;
import com.tcc.trilha_do_saber.service.LogAuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
public class VerificacaoController {

    private final EmailService emailService;
    private final LogAuditoriaService logAuditoriaService;
    private final SecureRandom random = new SecureRandom();

    public VerificacaoController(EmailService emailService, LogAuditoriaService logAuditoriaService){
        this.emailService = emailService;
        this.logAuditoriaService = logAuditoriaService;
    }

    @GetMapping("/verificacao")
    public String tela(HttpSession session, Model model){
        VerificacaoSessaoDTO verificacao = (VerificacaoSessaoDTO) session.getAttribute("verificacao2FA");
        if (verificacao == null) {
            return "redirect:/login";
        }
        model.addAttribute("email", verificacao.getUsuario().getEmail());
        if (!verificacao.isEmailEnviado()) {
            model.addAttribute("codigoGerado", verificacao.getCodigo());
        }
        return "verificacao";
    }

    @PostMapping("/verificacao")
    public String confirmar(@RequestParam String d1, @RequestParam String d2, @RequestParam String d3,
                            @RequestParam String d4, @RequestParam String d5, @RequestParam String d6,
                            HttpSession session, Model model, HttpServletRequest request){

        VerificacaoSessaoDTO verificacao = (VerificacaoSessaoDTO) session.getAttribute("verificacao2FA");
        if (verificacao == null) {
            return "redirect:/login";
        }

        long inicio = System.currentTimeMillis();
        String codigoDigitado = d1 + d2 + d3 + d4 + d5 + d6;

        if (verificacao.expirado()) {
            session.removeAttribute("verificacao2FA");
            model.addAttribute("erroVerificacao", "Código expirado. Faça login novamente.");
            return "redirect:/login";
        }

        if (!verificacao.getCodigo().equals(codigoDigitado)) {
            long duracao = System.currentTimeMillis() - inicio;
            logAuditoriaService.registrarSemAtor(verificacao.getUsuario().getEmail(), TipoAcao.LOGIN_FALHA,
                    "Login", "Código de verificação incorreto", request.getRemoteAddr(), duracao);
            model.addAttribute("email", verificacao.getUsuario().getEmail());
            model.addAttribute("erroVerificacao", "Código inválido. Confira e tente novamente.");
            return "verificacao";
        }

        UsuarioSessaoDTO usuario = verificacao.getUsuario();
        session.removeAttribute("verificacao2FA");
        session.setAttribute("usuarioLogado", usuario);

        long duracao = System.currentTimeMillis() - inicio;
        logAuditoriaService.registrar(usuario.getId(), usuario.getNome(), usuario.getTipo(),
                TipoAcao.LOGIN_SUCESSO, "Login", usuario.getId(), null,
                request.getRemoteAddr(), duracao);

        return "redirect:" + paginaInicial(usuario.getTipo());
    }

    @PostMapping("/verificacao/reenviar")
    public String reenviar(HttpSession session, Model model){
        VerificacaoSessaoDTO verificacao = (VerificacaoSessaoDTO) session.getAttribute("verificacao2FA");
        if (verificacao == null) {
            return "redirect:/login";
        }

        String novoCodigo = gerarCodigo();
        Instant novaExpiracao = Instant.now().plus(10, ChronoUnit.MINUTES);
        UsuarioSessaoDTO usuario = verificacao.getUsuario();

        boolean emailEnviado = emailService.enviarCodigoVerificacao(usuario.getNome(), usuario.getEmail(), novoCodigo);
        session.setAttribute("verificacao2FA", new VerificacaoSessaoDTO(usuario, novoCodigo, novaExpiracao, emailEnviado));

        model.addAttribute("email", usuario.getEmail());
        model.addAttribute("codigoReenviado", true);
        if (!emailEnviado) {
            model.addAttribute("codigoGerado", novoCodigo);
        }
        return "verificacao";
    }

    private String gerarCodigo(){
        int numero = random.nextInt(1_000_000);
        return String.format("%06d", numero);
    }

    private String paginaInicial(String tipo){
        return switch (tipo) {
            case "ALUNO" -> "/aluno/inicio";
            case "PROFESSOR" -> "/professor/inicio";
            case "COORDENADOR" -> "/coordenador/inicio";
            case "ADMIN" -> "/admin/inicio";
            default -> "/login";
        };
    }
}