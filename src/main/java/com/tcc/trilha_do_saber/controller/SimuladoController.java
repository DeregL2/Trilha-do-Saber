package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.model.SimuladoQuestao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.service.SimuladoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller // Diz ao Spring que essa classe atende requisições HTTP e devolve páginas
public class SimuladoController {

    private final SimuladoService simuladoService;
    private final AlternativaRepository alternativaRepository;

    // Construtor da Classe
    public SimuladoController(SimuladoService simuladoService, AlternativaRepository alternativaRepository) {
        this.simuladoService = simuladoService;
        this.alternativaRepository = alternativaRepository;
    }

    // Cria o simulado e manda o aluno direto para a primeira questao.
    @GetMapping("/simulado/iniciar")
    public String iniciar(@RequestParam(defaultValue = "Sistemas de Informação") String curso) {

        Simulado simulado = simuladoService.iniciar("Aluno", curso);

        return "redirect:/simulado/" + simulado.getId() + "/questao/1";
    }

    // Mostra uma questao do simulado. A ordem e a posicao dela: 1, 2, 3...
    @GetMapping("/simulado/{id}/questao/{ordem}")
    public String questao(@PathVariable Long id, @PathVariable int ordem, Model model) {

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

    // Recebe o envio do formulario. O botao clicado chega no parametro "acao".
    @PostMapping("/simulado/{id}/questao/{ordem}")
    public String responder(@PathVariable Long id,
                            @PathVariable int ordem,
                            @RequestParam(required = false) Long alternativaId,
                            @RequestParam String acao) {

        SimuladoQuestao simuladoQuestao = simuladoService.buscarQuestao(id, ordem);

        // required = false permite avancar sem marcar nada, deixando a questao em branco
        if (alternativaId != null) {
            simuladoService.responder(id, simuladoQuestao.getQuestao().getId(), alternativaId);
        }

        if ("finalizar".equals(acao)) {
            simuladoService.finalizar(id);
            return "redirect:/simulado/" + id + "/concluido";
        }

        int destino = "anterior".equals(acao) ? ordem - 1 : ordem + 1;

        // Trava de seguranca caso o destino saia do intervalo valido
        long total = simuladoService.totalQuestoes(id);
        if (destino < 1) {
            destino = 1;
        }
        if (destino > total) {
            destino = (int) total;
        }

        // Redireciona em vez de devolver a tela: assim o F5 recarrega a questao,
        // e nao reenvia o formulario.
        return "redirect:/simulado/" + id + "/questao/" + destino;
    }

    // Tela de encerramento. O calculo de acertos e os graficos sao a regra de negocio do retorno.
    @GetMapping("/simulado/{id}/concluido")
    public String concluido(@PathVariable Long id, Model model) {

        Simulado simulado = simuladoService.buscarSimulado(id);

        model.addAttribute("simulado", simulado);
        model.addAttribute("total", simuladoService.totalQuestoes(id));
        model.addAttribute("respondidas", simuladoService.totalRespondidas(id));

        return "simulado-concluido";
    }
}