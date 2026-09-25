package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.LogAuditoriaViewDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.LogAuditoria;
import com.tcc.trilha_do_saber.service.LogAuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/logs")
public class LogAuditoriaController {

    private final LogAuditoriaService logAuditoriaService;

    public LogAuditoriaController(LogAuditoriaService logAuditoriaService){
        this.logAuditoriaService = logAuditoriaService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session){
        UsuarioSessaoDTO usuarioLogado = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !"ADMIN".equals(usuarioLogado.getTipo())) {
            return "redirect:/login";
        }

        List<LogAuditoria> logs = logAuditoriaService.listarTodos();

        List<LogAuditoriaViewDTO> visao = logs.stream()
                .map(log -> new LogAuditoriaViewDTO(log, logAuditoriaService.verificarIntegridade(log)))
                .collect(Collectors.toList());

        model.addAttribute("logs", visao);
        model.addAttribute("usuarioLogado", usuarioLogado);
        return "admin/logs";
    }
}