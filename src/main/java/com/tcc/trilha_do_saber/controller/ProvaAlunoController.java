package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.service.ProvaDoAlunoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProvaAlunoController {

    private final ProvaDoAlunoService provaDoAlunoService;

    public ProvaAlunoController(ProvaDoAlunoService provaDoAlunoService) {
        this.provaDoAlunoService = provaDoAlunoService;
    }

    @PostMapping("/simulado/prova/{provaId}/iniciar")
    public String iniciar(@PathVariable Long provaId, HttpSession session, RedirectAttributes redirect) {

        Object logado = session.getAttribute("usuarioLogado");
        if (!(logado instanceof UsuarioSessaoDTO aluno) || !"ALUNO".equals(aluno.getTipo())) {
            return "redirect:/login";
        }

        try {
            Simulado simulado = provaDoAlunoService.iniciar(provaId, aluno.getId());
            return "redirect:/simulado/" + simulado.getId() + "/questao/1";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erroSimulado", e.getMessage());
            return "redirect:/simulado/novo";
        }
    }
}