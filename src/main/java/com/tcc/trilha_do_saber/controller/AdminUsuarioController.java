package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.CoordenadorFormDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Coordenador;
import com.tcc.trilha_do_saber.service.CoordenadorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/coordenadores")
public class AdminUsuarioController {

    private final CoordenadorService coordenadorService;

    // Construtor
    public AdminUsuarioController(CoordenadorService coordenadorService){
        this.coordenadorService = coordenadorService;
    }

    // Lista os coordenadores na tela do admin
    @GetMapping
    public String listar(Model model, HttpSession session){
        model.addAttribute("coordenadores", coordenadorService.listarTodos());
        model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
        return "admin/coordenadores";
    }

    // Monta o formulario de edicao ja preenchido com os dados atuais do coordenador
    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model, HttpSession session){
        Coordenador coordenador = coordenadorService.buscarPorId(id);

        CoordenadorFormDTO form = new CoordenadorFormDTO();
        form.setId(coordenador.getId());
        form.setNome(coordenador.getNome());
        form.setEmail(coordenador.getEmail());
        form.setRegistroFuncional(coordenador.getRegistroFuncional());
        form.setCurso(coordenador.getCurso());
        form.setAtivo(coordenador.isAtivo());

        model.addAttribute("coordenadorForm", form);
        model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
        return "admin/coordenadorForm";
    }

    // Salva as alteracoes do formulario; exige usuario logado, senao manda pro login em vez de quebrar
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("coordenadorForm") CoordenadorFormDTO form,
                            BindingResult result, HttpSession session, HttpServletRequest request){
        if (result.hasErrors()) {
            return "admin/coordenadorForm";
        }
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        // Mede o tempo da operacao pra registrar no log de auditoria junto com quem fez e de onde
        long inicio = System.currentTimeMillis();
        coordenadorService.atualizar(id, form, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/admin/coordenadores";
    }

    // Aprova o cadastro do coordenador
    @PostMapping("/{id}/aprovar")
    public String aprovar(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        coordenadorService.ativar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/admin/coordenadores";
    }

    // Bloqueia o acesso do coordenador
    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        coordenadorService.desativar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/admin/coordenadores";
    }

    // Exclui (soft delete) o coordenador
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        coordenadorService.excluir(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/admin/coordenadores";
    }

    // Anonimiza definitivamente os dados pessoais do coordenador
    @PostMapping("/{id}/anonimizar")
    public String anonimizar(@PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        coordenadorService.anonimizar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/admin/coordenadores";
    }
}