package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.TurmaDoProfessorDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.service.DashboardAlunoService;
import com.tcc.trilha_do_saber.service.PainelAdminService;
import com.tcc.trilha_do_saber.service.PainelCoordenadorService;
import com.tcc.trilha_do_saber.service.ProfessorPainelService;
import com.tcc.trilha_do_saber.service.ProfessorTurmaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class HomeController {

    private static final MonthDay DIA_PADRAO_ENADE = MonthDay.of(11, 29);

    @Value("${app.enade.data:}")
    private String dataEnadeConfigurada;

    private final ProfessorTurmaService professorTurmaService;
    private final ProfessorPainelService professorPainelService;
    private final DashboardAlunoService dashboardAlunoService;
    private final PainelCoordenadorService painelCoordenadorService;
    private final PainelAdminService painelAdminService;

    public HomeController(ProfessorTurmaService professorTurmaService, ProfessorPainelService professorPainelService,
                          DashboardAlunoService dashboardAlunoService, PainelCoordenadorService painelCoordenadorService,
                          PainelAdminService painelAdminService) {
        this.professorTurmaService = professorTurmaService;
        this.professorPainelService = professorPainelService;
        this.dashboardAlunoService = dashboardAlunoService;
        this.painelCoordenadorService = painelCoordenadorService;
        this.painelAdminService = painelAdminService;
    }

    @GetMapping("/aluno/inicio")
    public String inicioAluno(HttpSession session, Model model){
        UsuarioSessaoDTO usuario = usuarioDoTipo(session, "ALUNO");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("dashboard", dashboardAlunoService.montar(usuario.getId()));
        adicionarContagemEnade(model);
        return "aluno/inicio";
    }

    @GetMapping("/professor/inicio")
    public String inicioProfessor(HttpSession session, Model model){
        UsuarioSessaoDTO usuario = usuarioDoTipo(session, "PROFESSOR");
        if (usuario == null) {
            return "redirect:/login";
        }
        List<String> periodos = professorTurmaService.periodos(usuario.getId());
        String periodo = professorTurmaService.escolherPeriodo(periodos, null);
        List<TurmaDoProfessorDTO> turmas = professorTurmaService.turmas(usuario.getId(), periodo);

        model.addAttribute("usuario", usuario);
        model.addAttribute("periodoSelecionado", periodo);
        model.addAttribute("painel", professorPainelService.montar(turmas, periodo));
        adicionarContagemEnade(model);
        return "professor/inicio";
    }

    @GetMapping("/coordenador/inicio")
    public String inicioCoordenador(HttpSession session, Model model){
        UsuarioSessaoDTO usuario = usuarioDoTipo(session, "COORDENADOR");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("painel", painelCoordenadorService.montar());
        adicionarContagemEnade(model);
        return "coordenador/inicio";
    }

    @GetMapping("/admin/inicio")
    public String inicioAdmin(HttpSession session, Model model){
        UsuarioSessaoDTO usuario = usuarioDoTipo(session, "ADMIN");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("painel", painelAdminService.montar());
        adicionarContagemEnade(model);
        return "admin/inicio";
    }

    private UsuarioSessaoDTO usuarioDoTipo(HttpSession session, String tipo){
        Object usuario = session.getAttribute("usuarioLogado");
        if (!(usuario instanceof UsuarioSessaoDTO usuarioSessao) || !tipo.equals(usuarioSessao.getTipo())) {
            return null;
        }
        return usuarioSessao;
    }

    private void adicionarContagemEnade(Model model){
        LocalDate hoje = LocalDate.now();
        LocalDate dataEnade = proximoEnade(hoje, dataEnadeConfigurada);
        model.addAttribute("dataEnade", dataEnade);
        model.addAttribute("diasParaEnade", ChronoUnit.DAYS.between(hoje, dataEnade));
    }

    // Usa a data oficial configurada em app.enade.data (AAAA-MM-DD) enquanto ela não passou;
    // sem data configurada, ou depois dela, conta até o próximo 29/11 para nunca mostrar dias negativos.
    static LocalDate proximoEnade(LocalDate hoje, String dataConfigurada){
        if (dataConfigurada != null && !dataConfigurada.isBlank()) {
            try {
                LocalDate configurada = LocalDate.parse(dataConfigurada.trim());
                if (!configurada.isBefore(hoje)) {
                    return configurada;
                }
            } catch (DateTimeParseException ignorada) {
                // data mal escrita na configuração: segue com a data padrão
            }
        }
        LocalDate padrao = LocalDate.of(hoje.getYear(), DIA_PADRAO_ENADE.getMonth(), DIA_PADRAO_ENADE.getDayOfMonth());
        return padrao.isBefore(hoje) ? padrao.plusYears(1) : padrao;
    }
}