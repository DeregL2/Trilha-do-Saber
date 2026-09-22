package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.LoginDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService){
        this.authService = authService;
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
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:" + paginaInicial(usuario.getTipo());
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
