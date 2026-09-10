package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.model.Resposta;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.service.SimuladoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // Devolve texto puro em vez de renderizar HTML
public class SimuladoTesteController {

    private final SimuladoService simuladoService;

    public SimuladoTesteController(SimuladoService simuladoService) {
        this.simuladoService = simuladoService;
    }

    @GetMapping("/teste/iniciar")
    public String iniciar(@RequestParam String curso) {
        Simulado simulado = simuladoService.iniciar("Thaissa", curso);
        return "Simulado criado com id " + simulado.getId()
                + " | titulo: " + simulado.getTitulo()
                + " | duracao: " + simulado.getDuracaoMinutos() + " min"
                + " | questoes: " + simuladoService.totalQuestoes(simulado.getId());
    }

    @GetMapping("/teste/responder")
    public String responder(@RequestParam Long simuladoId,
                            @RequestParam Long questaoId,
                            @RequestParam Long alternativaId) {

        Resposta resposta = simuladoService.responder(simuladoId, questaoId, alternativaId);
        return "Resposta id " + resposta.getId()
                + " | acertou: " + resposta.isAcertou()
                + " | respondidas: " + simuladoService.totalRespondidas(simuladoId)
                + " de " + simuladoService.totalQuestoes(simuladoId);
    }

    @GetMapping("/teste/finalizar")
    public String finalizar(@RequestParam Long simuladoId) {
        Simulado simulado = simuladoService.finalizar(simuladoId);
        return "Simulado finalizado em: " + simulado.getDataFim();
    }}