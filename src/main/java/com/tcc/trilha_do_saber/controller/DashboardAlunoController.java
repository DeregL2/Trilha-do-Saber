package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.service.DashboardAlunoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardAlunoController {

    private final DashboardAlunoService dashboardAlunoService;

    public DashboardAlunoController(DashboardAlunoService dashboardAlunoService) {
        this.dashboardAlunoService = dashboardAlunoService;
    }

    @GetMapping("/aluno/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Object logado = session.getAttribute("usuarioLogado");
        if (!(logado instanceof UsuarioSessaoDTO usuario) || !"ALUNO".equals(usuario.getTipo())) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("dashboard", dashboardAlunoService.montar(usuario.getId()));
        return "aluno/dashboard";
    }
}