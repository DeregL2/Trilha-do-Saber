package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.AlunoFormDTO;
import com.tcc.trilha_do_saber.dto.ProfessorFormDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.model.Professor;
import com.tcc.trilha_do_saber.service.AlunoService;
import com.tcc.trilha_do_saber.service.ProfessorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/coordenador/usuarios")
public class CoordenadorUsuarioController {

    private final AlunoService alunoService;
    private final ProfessorService professorService;

    // Construtor
    public CoordenadorUsuarioController(AlunoService alunoService, ProfessorService professorService){
        this.alunoService = alunoService;
        this.professorService = professorService;
    }

    // Lista alunos e professores na tela da coordenacao
    @GetMapping
    public String listar(Model model, HttpSession session){
        model.addAttribute("alunos", alunoService.listarTodos());
        model.addAttribute("professores", professorService.listarTodos());
        model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
        return "coordenador/usuarios";
    }

    // Monta o formulario de edicao ja preenchido, de aluno ou professor conforme o "tipo" da URL
    @GetMapping("/{tipo}/{id}/editar")
    public String editarFormulario(@PathVariable String tipo, @PathVariable Long id, Model model, HttpSession session){
        if ("professor".equals(tipo)) {
            Professor professor = professorService.buscarPorId(id);
            ProfessorFormDTO form = new ProfessorFormDTO();
            form.setId(professor.getId());
            form.setNome(professor.getNome());
            form.setEmail(professor.getEmail());
            form.setRegistroProfissional(professor.getRegistroProfissional());
            form.setDisciplina(professor.getDisciplina());
            form.setAtivo(professor.isAtivo());
            model.addAttribute("professorForm", form);
        } else {
            Aluno aluno = alunoService.buscarPorId(id);
            AlunoFormDTO form = new AlunoFormDTO();
            form.setId(aluno.getId());
            form.setNome(aluno.getNome());
            form.setEmail(aluno.getEmail());
            form.setRgm(aluno.getRgm());
            form.setCurso(aluno.getCurso());
            form.setSemestre(aluno.getSemestre());
            form.setAtivo(aluno.isAtivo());
            model.addAttribute("alunoForm", form);
        }
        model.addAttribute("tipo", tipo);
        model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
        return "coordenador/usuarioForm";
    }

    // Salva as alteracoes de um aluno; exige usuario logado, senao manda pro login em vez de quebrar
    @PostMapping("/aluno/{id}")
    public String atualizarAluno(@PathVariable Long id, @Valid @ModelAttribute("alunoForm") AlunoFormDTO form,
                                 BindingResult result, Model model, HttpSession session, HttpServletRequest request){
        if (result.hasErrors()) {
            model.addAttribute("tipo", "aluno");
            return "coordenador/usuarioForm";
        }
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        alunoService.atualizar(id, form, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios";
    }

    // Salva as alteracoes de um professor; mesma logica do aluno
    @PostMapping("/professor/{id}")
    public String atualizarProfessor(@PathVariable Long id, @Valid @ModelAttribute("professorForm") ProfessorFormDTO form,
                                     BindingResult result, Model model, HttpSession session, HttpServletRequest request){
        if (result.hasErrors()) {
            model.addAttribute("tipo", "professor");
            return "coordenador/usuarioForm";
        }
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        professorService.atualizar(id, form, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios";
    }

    // Exclui (soft delete) aluno ou professor, conforme o "tipo" da URL
    @PostMapping("/{tipo}/{id}/excluir")
    public String excluir(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        if ("professor".equals(tipo)) {
            professorService.excluir(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        } else {
            alunoService.excluir(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        }
        return "redirect:/coordenador/usuarios";
    }

    // Anonimiza definitivamente os dados pessoais de aluno ou professor, conforme o "tipo" da URL
    @PostMapping("/{tipo}/{id}/anonimizar")
    public String anonimizar(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        if ("professor".equals(tipo)) {
            professorService.anonimizar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        } else {
            alunoService.anonimizar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        }
        return "redirect:/coordenador/usuarios";
    }
}