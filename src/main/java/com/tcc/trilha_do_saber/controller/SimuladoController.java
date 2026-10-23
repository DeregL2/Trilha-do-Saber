package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.ResultadoSimuladoDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.exception.SemQuestoesException;
import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.model.SimuladoQuestao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.service.SimuladoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SimuladoController {

    private final SimuladoService simuladoService;
    private final AlternativaRepository alternativaRepository;

    public SimuladoController(SimuladoService simuladoService, AlternativaRepository alternativaRepository) {
        this.simuladoService = simuladoService;
        this.alternativaRepository = alternativaRepository;
    }

    @GetMapping("/simulado/iniciar")
    public String iniciar(HttpSession session, RedirectAttributes redirectAttributes) {

        UsuarioSessaoDTO aluno = alunoLogado(session);
        if (aluno == null) {
            return "redirect:/login";
        }

        try {
            Simulado simulado = simuladoService.iniciar(aluno.getId());
            return "redirect:/simulado/" + simulado.getId() + "/questao/1";
        } catch (SemQuestoesException e) {
            redirectAttributes.addFlashAttribute("erroSimulado",
                    "Ainda não há questões cadastradas para o curso " + e.getCurso() + ". Fale com a coordenação.");
            return "redirect:/aluno/dashboard";
        }
    }

    @GetMapping("/simulado/{id}/questao/{ordem}")
    public String questao(@PathVariable Long id, @PathVariable int ordem, Model model, HttpSession session) {

        String bloqueio = verificarAcesso(id, session);
        if (bloqueio != null) {
            return bloqueio;
        }

        Simulado simulado = simuladoService.buscarSimulado(id);

        if (simulado.isFinalizado()) {
            return "redirect:/simulado/" + id + "/concluido";
        }

        SimuladoQuestao simuladoQuestao = simuladoService.buscarQuestao(id, ordem);
        Questao questao = simuladoQuestao.getQuestao();

        List<Alternativa> alternativas = alternativaRepository.findByQuestaoIdOrderByIdAsc(questao.getId());

        Long alternativaSelecionadaId = simuladoService.respostaDaQuestao(id, questao.getId())
                .map(resposta -> resposta.getAlternativaEscolhida().getId())
                .orElse(null);

        long total = simuladoService.totalQuestoes(id);
        long respondidas = simuladoService.totalRespondidas(id);

        long segundosRestantes = Duration.between(LocalDateTime.now(), simulado.getDataLimite()).getSeconds();
        if (segundosRestantes < 0) {
            segundosRestantes = 0;
        }

        model.addAttribute("simulado", simulado);
        model.addAttribute("questao", questao);
        model.addAttribute("alternativas", alternativas);
        model.addAttribute("alternativaSelecionadaId", alternativaSelecionadaId);
        model.addAttribute("ordem", ordem);
        model.addAttribute("total", total);
        model.addAttribute("respondidas", respondidas);
        model.addAttribute("emBranco", total - respondidas);
        model.addAttribute("progresso", (ordem * 100) / total);
        model.addAttribute("segundosRestantes", segundosRestantes);

        return "simulado";
    }

    @PostMapping("/simulado/{id}/questao/{ordem}")
    public String responder(@PathVariable Long id,
                            @PathVariable int ordem,
                            @RequestParam(required = false) Long alternativaId,
                            @RequestParam String acao,
                            HttpSession session) {

        String bloqueio = verificarAcesso(id, session);
        if (bloqueio != null) {
            return bloqueio;
        }

        SimuladoQuestao simuladoQuestao = simuladoService.buscarQuestao(id, ordem);

        if (alternativaId != null) {
            simuladoService.responder(id, simuladoQuestao.getQuestao().getId(), alternativaId);
        }

        if ("finalizar".equals(acao)) {
            simuladoService.finalizar(id);
            return "redirect:/simulado/" + id + "/resultado";
        }

        int destino = "anterior".equals(acao) ? ordem - 1 : ordem + 1;

        long total = simuladoService.totalQuestoes(id);
        if (destino < 1) {
            destino = 1;
        }
        if (destino > total) {
            destino = (int) total;
        }

        return "redirect:/simulado/" + id + "/questao/" + destino;
    }

    @GetMapping("/simulado/{id}/concluido")
    public String concluido(@PathVariable Long id, Model model, HttpSession session) {

        String bloqueio = verificarAcesso(id, session);
        if (bloqueio != null) {
            return bloqueio;
        }

        Simulado simulado = simuladoService.buscarSimulado(id);

        model.addAttribute("simulado", simulado);
        model.addAttribute("total", simuladoService.totalQuestoes(id));
        model.addAttribute("respondidas", simuladoService.totalRespondidas(id));

        return "simuladoConcluido";
    }

    @GetMapping("/simulado/{id}/resultado")
    public String verResultado(@PathVariable Long id, Model model, HttpSession session) {

        String bloqueio = verificarAcesso(id, session);
        if (bloqueio != null) {
            return bloqueio;
        }

        if (!simuladoService.buscarSimulado(id).isFinalizado()) {
            return "redirect:/simulado/" + id + "/questao/1";
        }

        ResultadoSimuladoDTO resultado = simuladoService.gerarResultado(id);
        model.addAttribute("resultado", resultado);
        return "resultado";
    }

    private UsuarioSessaoDTO alunoLogado(HttpSession session) {
        Object logado = session.getAttribute("usuarioLogado");
        if (logado instanceof UsuarioSessaoDTO usuario && "ALUNO".equals(usuario.getTipo())) {
            return usuario;
        }
        return null;
    }

    private String verificarAcesso(Long simuladoId, HttpSession session) {
        UsuarioSessaoDTO aluno = alunoLogado(session);
        if (aluno == null) {
            return "redirect:/login";
        }
        if (!simuladoService.pertenceAoAluno(simuladoId, aluno.getId())) {
            return "redirect:/aluno/dashboard";
        }
        return null;
    }
}