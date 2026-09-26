package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.CadastroAlunoDTO;
import com.tcc.trilha_do_saber.dto.CadastroCoordenadorDTO;
import com.tcc.trilha_do_saber.dto.CadastroProfessorDTO;
import com.tcc.trilha_do_saber.service.AlunoService;
import com.tcc.trilha_do_saber.service.CoordenadorService;
import com.tcc.trilha_do_saber.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final CoordenadorService coordenadorService;

    public CadastroController(AlunoService alunoService, ProfessorService professorService, CoordenadorService coordenadorService){
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.coordenadorService = coordenadorService;
    }

    @GetMapping("/aluno")
    public String formularioAluno(Model model){
        model.addAttribute("cadastroAluno", new CadastroAlunoDTO());
        return "cadastro/solicitarAcessoAluno";
    }

    @PostMapping("/aluno")
    public String cadastrarAluno(@Valid @ModelAttribute("cadastroAluno") CadastroAlunoDTO dto, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "cadastro/solicitarAcessoAluno";
        }
        if (!dto.senhasConferem()) {
            model.addAttribute("erroSenha", "As senhas não conferem");
            return "cadastro/solicitarAcessoAluno";
        }

        try {
            alunoService.cadastrar(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroSenha", e.getMessage());
            return "cadastro/solicitarAcessoAluno";
        }

        return "redirect:/login?cadastroEnviado";
    }

    @GetMapping("/professor")
    public String formularioProfessor(Model model){
        model.addAttribute("cadastroProfessor", new CadastroProfessorDTO());
        return "cadastro/solicitarAcessoProfessor";
    }

    @PostMapping("/professor")
    public String cadastrarProfessor(@Valid @ModelAttribute("cadastroProfessor") CadastroProfessorDTO dto, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "cadastro/solicitarAcessoProfessor";
        }
        if (!dto.senhasConferem()) {
            model.addAttribute("erroSenha", "As senhas não conferem");
            return "cadastro/solicitarAcessoProfessor";
        }

        try {
            professorService.cadastrar(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroSenha", e.getMessage());
            return "cadastro/solicitarAcessoProfessor";
        }

        return "redirect:/login?cadastroEnviado";
    }

    @GetMapping("/termos-lgpd")
    public String termosLgpd(){
        return "cadastro/termosLgpd";
    }

    @GetMapping("/coordenador")
    public String formularioCoordenador(Model model){
        model.addAttribute("cadastroCoordenador", new CadastroCoordenadorDTO());
        return "cadastro/solicitarAcessoCoordenador";
    }

    @PostMapping("/coordenador")
    public String cadastrarCoordenador(@Valid @ModelAttribute("cadastroCoordenador") CadastroCoordenadorDTO dto, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "cadastro/solicitarAcessoCoordenador";
        }
        if (!dto.senhasConferem()) {
            model.addAttribute("erroSenha", "As senhas não conferem");
            return "cadastro/solicitarAcessoCoordenador";
        }

        try {
            coordenadorService.cadastrar(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroSenha", e.getMessage());
            return "cadastro/solicitarAcessoCoordenador";
        }

        return "redirect:/login?cadastroEnviado";
    }
}

