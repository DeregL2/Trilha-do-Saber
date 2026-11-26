package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.AlunoFormDTO;
import com.tcc.trilha_do_saber.dto.PaginacaoDTO;
import com.tcc.trilha_do_saber.dto.ProfessorFormDTO;
import com.tcc.trilha_do_saber.dto.SolicitacaoContaDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.exception.AcessoNegadoException;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.model.Professor;
import com.tcc.trilha_do_saber.service.AlunoService;
import com.tcc.trilha_do_saber.service.ImportacaoAlunosService;
import com.tcc.trilha_do_saber.service.MinhaContaService;
import com.tcc.trilha_do_saber.service.ProfessorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/coordenador/usuarios")
public class CoordenadorUsuarioController {

    static final int TAMANHO_PAGINA = 10;

    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final MinhaContaService minhaContaService;
    private final ImportacaoAlunosService importacaoAlunosService;

    public CoordenadorUsuarioController(AlunoService alunoService, ProfessorService professorService,
                                        MinhaContaService minhaContaService,
                                        ImportacaoAlunosService importacaoAlunosService){
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.minhaContaService = minhaContaService;
        this.importacaoAlunosService = importacaoAlunosService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String aba,
                         @RequestParam(required = false) String busca,
                         @RequestParam(defaultValue = "1") int pa,
                         @RequestParam(defaultValue = "1") int pp,
                         Model model, HttpSession session){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        List<Aluno> alunos = dosCursosPermitidos(ator, alunoService.listarTodos());
        List<Professor> professores = professorService.listarTodos();
        String termo = busca == null ? "" : busca.trim();

        PaginacaoDTO<Aluno> paginaAlunos = PaginacaoDTO.de(filtrarAlunos(alunos, termo), pa, TAMANHO_PAGINA);
        PaginacaoDTO<Professor> paginaProfessores = PaginacaoDTO.de(professores, pp, TAMANHO_PAGINA);

        model.addAttribute("alunos", alunos);
        model.addAttribute("professores", professores);
        model.addAttribute("paginaAlunos", paginaAlunos);
        model.addAttribute("paginaProfessores", paginaProfessores);
        model.addAttribute("busca", termo);
        model.addAttribute("linkAlunos", LinkPaginacao.montar("/coordenador/usuarios", "pa", Map.of(
                "busca", termo,
                "pp", String.valueOf(paginaProfessores.getPaginaAtual()))));
        model.addAttribute("linkProfessores", LinkPaginacao.montar("/coordenador/usuarios", "pp", Map.of(
                "busca", termo,
                "pa", String.valueOf(paginaAlunos.getPaginaAtual()))));
        model.addAttribute("solicitacoes", solicitacoesVisiveis(ator));
        model.addAttribute("abaAtiva", "solicitacoes".equals(aba) ? "solicitacoes" : "usuarios");
        model.addAttribute("usuarioLogado", ator);
        return "coordenador/usuarios";
    }

    @GetMapping("/{tipo}/{id}/editar")
    public String editarFormulario(@PathVariable String tipo, @PathVariable Long id, Model model, HttpSession session){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }

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
            Aluno aluno = exigirAlunoDoCurso(ator, id);
            AlunoFormDTO form = new AlunoFormDTO();
            form.setId(aluno.getId());
            form.setNome(aluno.getNome());
            form.setEmail(aluno.getEmail());
            form.setRegistroAluno(aluno.getRegistroAluno());
            form.setCurso(aluno.getCurso());
            form.setSemestre(aluno.getSemestre());
            form.setTurma(aluno.getTurma());
            form.setTurno(aluno.getTurno());
            form.setAtivo(aluno.isAtivo());
            model.addAttribute("alunoForm", form);
        }
        model.addAttribute("tipo", tipo);
        model.addAttribute("usuarioLogado", ator);
        return "coordenador/usuarioForm";
    }

    @PostMapping("/aluno/{id}")
    public String atualizarAluno(@PathVariable Long id, @Valid @ModelAttribute("alunoForm") AlunoFormDTO form,
                                 BindingResult result, Model model, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        exigirAlunoDoCurso(ator, id);
        if (!result.hasFieldErrors("curso") && !cursoPermitido(ator, form.getCurso())) {
            result.rejectValue("curso", "curso.naoPermitido", "Escolha um curso que você coordena");
        }
        if (result.hasErrors()) {
            model.addAttribute("tipo", "aluno");
            model.addAttribute("usuarioLogado", ator);
            return "coordenador/usuarioForm";
        }
        long inicio = System.currentTimeMillis();
        alunoService.atualizar(id, form, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios";
    }

    @PostMapping("/professor/{id}")
    public String atualizarProfessor(@PathVariable Long id, @Valid @ModelAttribute("professorForm") ProfessorFormDTO form,
                                     BindingResult result, Model model, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            model.addAttribute("tipo", "professor");
            model.addAttribute("usuarioLogado", ator);
            return "coordenador/usuarioForm";
        }
        long inicio = System.currentTimeMillis();
        professorService.atualizar(id, form, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios";
    }

    @PostMapping("/{tipo}/{id}/excluir")
    public String excluir(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        if ("professor".equals(tipo)) {
            professorService.excluir(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        } else {
            exigirAlunoDoCurso(ator, id);
            alunoService.excluir(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        }
        return "redirect:/coordenador/usuarios";
    }

    @PostMapping("/{tipo}/{id}/anonimizar")
    public String anonimizar(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        long inicio = System.currentTimeMillis();
        if ("professor".equals(tipo)) {
            professorService.anonimizar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        } else {
            exigirAlunoDoCurso(ator, id);
            alunoService.anonimizar(id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        }
        return "redirect:/coordenador/usuarios";
    }

    @PostMapping("/solicitacoes/{tipo}/{id}/aprovar")
    public String aprovarSolicitacao(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        exigirSolicitacaoGerenciavel(ator, tipo, id);
        long inicio = System.currentTimeMillis();
        minhaContaService.aprovarSolicitacao(tipo.toUpperCase(), id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios?aba=solicitacoes";
    }

    @PostMapping("/solicitacoes/{tipo}/{id}/recusar")
    public String recusarSolicitacao(@PathVariable String tipo, @PathVariable Long id, HttpSession session, HttpServletRequest request){
        UsuarioSessaoDTO ator = (UsuarioSessaoDTO) session.getAttribute("usuarioLogado");
        if (ator == null || !("ADMIN".equals(ator.getTipo()) || "COORDENADOR".equals(ator.getTipo()))) {
            return "redirect:/login";
        }
        exigirSolicitacaoGerenciavel(ator, tipo, id);
        long inicio = System.currentTimeMillis();
        minhaContaService.recusarSolicitacao(tipo.toUpperCase(), id, ator, request.getRemoteAddr(), System.currentTimeMillis() - inicio);
        return "redirect:/coordenador/usuarios?aba=solicitacoes";
    }

    // Coordenador só gerencia alunos dos cursos que coordena; o admin gerencia todos.
    private Aluno exigirAlunoDoCurso(UsuarioSessaoDTO ator, Long alunoId){
        Aluno aluno = alunoService.buscarPorId(alunoId);
        if (!cursoPermitido(ator, aluno.getCurso())) {
            throw new AcessoNegadoException("Este aluno é de um curso que você não coordena");
        }
        return aluno;
    }

    private boolean cursoPermitido(UsuarioSessaoDTO ator, String curso){
        if ("ADMIN".equals(ator.getTipo())) {
            return true;
        }
        return curso != null && importacaoAlunosService.cursosPermitidos(ator).stream()
                .anyMatch(permitido -> permitido.equalsIgnoreCase(curso.trim()));
    }

    private List<Aluno> dosCursosPermitidos(UsuarioSessaoDTO ator, List<Aluno> alunos){
        if ("ADMIN".equals(ator.getTipo())) {
            return alunos;
        }
        return alunos.stream().filter(aluno -> cursoPermitido(ator, aluno.getCurso())).toList();
    }

    private List<SolicitacaoContaDTO> solicitacoesVisiveis(UsuarioSessaoDTO ator){
        List<SolicitacaoContaDTO> todas = minhaContaService.listarSolicitacoesDeAlunosEProfessores();
        if ("ADMIN".equals(ator.getTipo())) {
            return todas;
        }
        return todas.stream()
                .filter(s -> !"ALUNO".equals(s.getTipo()) || cursoPermitido(ator, alunoService.buscarPorId(s.getId()).getCurso()))
                .toList();
    }

    // Pedidos de coordenadores são do administrador; pedidos de alunos respeitam o curso do coordenador.
    private void exigirSolicitacaoGerenciavel(UsuarioSessaoDTO ator, String tipo, Long id){
        String tipoNormalizado = tipo == null ? "" : tipo.toUpperCase(Locale.ROOT);
        if (!"ALUNO".equals(tipoNormalizado) && !"PROFESSOR".equals(tipoNormalizado)) {
            throw new AcessoNegadoException("Somente o administrador analisa pedidos de coordenadores");
        }
        if ("ALUNO".equals(tipoNormalizado)) {
            exigirAlunoDoCurso(ator, id);
        }
    }

    static List<Aluno> filtrarAlunos(List<Aluno> alunos, String busca){
        List<String> termos = Arrays.stream(normalizar(busca).split("\\s+"))
                .filter(termo -> !termo.isEmpty())
                .toList();
        if (termos.isEmpty()) {
            return alunos;
        }
        return alunos.stream()
                .filter(aluno -> {
                    String texto = textoDeBusca(aluno);
                    return termos.stream().allMatch(texto::contains);
                })
                .toList();
    }

    private static String textoDeBusca(Aluno aluno){
        String status;
        if (aluno.isPrimeiroAcessoPendente()) {
            status = "Aguardando 1º acesso";
        } else if (aluno.isAtivo()) {
            status = "Ativo";
        } else if (aluno.isExcluido()) {
            status = "Excluído";
        } else {
            status = "Pendente";
        }
        String duasEtapas = aluno.isDuasEtapasAtiva() ? "Ligada" : "Desligada";

        return normalizar(String.join(" ",
                Objects.toString(aluno.getNome(), ""),
                Objects.toString(aluno.getEmail(), ""),
                Objects.toString(aluno.getRegistroAluno(), ""),
                Objects.toString(aluno.getCurso(), ""),
                Objects.toString(aluno.getSemestre(), ""),
                Objects.toString(aluno.getTurma(), ""),
                Objects.toString(aluno.getTurno(), ""),
                status,
                duasEtapas));
    }

    private static String normalizar(String texto){
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }
}