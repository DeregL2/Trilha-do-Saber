package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.LoginDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.dto.VerificacaoSessaoDTO;
import com.tcc.trilha_do_saber.service.AuthService;
import com.tcc.trilha_do_saber.service.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
public class LoginController {

    private final AuthService authService;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public LoginController(AuthService authService, EmailService emailService){
        this.authService = authService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String tela(Model model){
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@Valid @ModelAttribute("loginDTO") LoginDTO dto, BindingResult result, Model model, HttpSession session){
        if (result.hasErrors()) {
            return "login";
        }

        try {
            UsuarioSessaoDTO usuario = authService.autenticar(dto.getEmail(), dto.getSenha());

            String codigo = gerarCodigo();
            Instant expiraEm = Instant.now().plus(10, ChronoUnit.MINUTES);
            session.setAttribute("verificacao2FA", new VerificacaoSessaoDTO(usuario, codigo, expiraEm));

            emailService.enviarCodigoVerificacao(usuario.getNome(), usuario.getEmail(), codigo);

            return "redirect:/verificacao";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroLogin", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String sair(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    private String gerarCodigo(){
        int numero = random.nextInt(1_000_000);
        return String.format("%06d", numero);
    }
}
