package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Resposta;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.model.SimuladoQuestao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import com.tcc.trilha_do_saber.repository.RespostaRepository;
import com.tcc.trilha_do_saber.repository.SimuladoQuestaoRepository;
import com.tcc.trilha_do_saber.repository.SimuladoRepository;
import com.tcc.trilha_do_saber.dto.ResultadoSimuladoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SimuladoService {

    //NÃO MUDAR O MINUTOS_POR_QUESTAO
    private static final int QUESTOES_POR_SIMULADO = 5;
    private static final int MINUTOS_POR_QUESTAO = 6;

    private final SimuladoRepository simuladoRepository;
    private final SimuladoQuestaoRepository simuladoQuestaoRepository;
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final RespostaRepository respostaRepository;

    // Construtor da Classe
    public SimuladoService(SimuladoRepository simuladoRepository,
                           SimuladoQuestaoRepository simuladoQuestaoRepository,
                           QuestaoRepository questaoRepository,
                           AlternativaRepository alternativaRepository,
                           RespostaRepository respostaRepository) {

        this.simuladoRepository = simuladoRepository;
        this.simuladoQuestaoRepository = simuladoQuestaoRepository;
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.respostaRepository = respostaRepository;
    }

    @Transactional
    public Simulado iniciar(String nomeUsuario, String curso) {

        // Busca so as questoes das provas desse curso, de qualquer ano.
        List<Questao> disponiveis = new ArrayList<>(questaoRepository.findByProvaCurso(curso));

        if (disponiveis.isEmpty()) {
            throw new RuntimeException("Nao ha questoes cadastradas para o curso " + curso);
        }

        Collections.shuffle(disponiveis);

        int quantidade = Math.min(QUESTOES_POR_SIMULADO, disponiveis.size());

        int duracao = quantidade * MINUTOS_POR_QUESTAO;

        Simulado simulado = simuladoRepository.save(
                new Simulado(nomeUsuario, "Simulado ENADE - " + curso, duracao, LocalDateTime.now()));

        // Grava quais questoes cairam e em que posicao. A ordem comeca em 1.
        for (int i = 0; i < quantidade; i++) {
            simuladoQuestaoRepository.save(new SimuladoQuestao(simulado, disponiveis.get(i), i + 1));
        }

        return simulado;
    }

    // Registra a resposta. Se o aluno ja tinha respondido essa questao, atualiza em vez de duplicar.
    @Transactional
    public Resposta responder(Long simuladoId, Long questaoId, Long alternativaId) {

        LocalDateTime agora = LocalDateTime.now();

        Simulado simulado = simuladoRepository.findById(simuladoId)
                .orElseThrow(() -> new RuntimeException("Simulado nao encontrado"));

        if (!simulado.estaEmAndamento(agora)) {
            throw new RuntimeException("Simulado encerrado");
        }

        simuladoQuestaoRepository.findBySimuladoIdAndQuestaoId(simuladoId, questaoId)
                .orElseThrow(() -> new RuntimeException("Questao nao pertence a este simulado"));

        Alternativa alternativa = alternativaRepository.findById(alternativaId)
                .orElseThrow(() -> new RuntimeException("Alternativa nao encontrada"));

        if (!alternativa.getQuestao().getId().equals(questaoId)) {
            throw new RuntimeException("Alternativa nao pertence a esta questao");
        }

        boolean acertou = alternativa.isCorreta();

        Optional<Resposta> jaRespondida = respostaRepository.findBySimuladoIdAndQuestaoId(simuladoId, questaoId);

        // Botao anterior nao cria linhas novas, altera as que ja existem.
        if (jaRespondida.isPresent()) {
            Resposta resposta = jaRespondida.get();
            resposta.alterarResposta(alternativa, acertou, agora);
            return respostaRepository.save(resposta);
        }

        Questao questao = questaoRepository.findById(questaoId)
                .orElseThrow(() -> new RuntimeException("Questao nao encontrada"));

        return respostaRepository.save(
                new Resposta(simulado.getNomeUsuario(), questao, alternativa, acertou, agora, simulado));
    }

    // Encerra o simulado. A partir daqui nenhuma resposta e aceita.
    @Transactional
    public Simulado finalizar(Long simuladoId) {

        Simulado simulado = simuladoRepository.findById(simuladoId)
                .orElseThrow(() -> new RuntimeException("Simulado nao encontrado"));

        if (!simulado.isFinalizado()) {
            simulado.finalizar(LocalDateTime.now());
            simuladoRepository.save(simulado);
        }

        return simulado;
    }

    public SimuladoQuestao buscarQuestao(Long simuladoId, int ordem) {
        return simuladoQuestaoRepository.findBySimuladoIdAndOrdem(simuladoId, ordem)
                .orElseThrow(() -> new RuntimeException("Questao nao encontrada no simulado"));
    }

    public Simulado buscarSimulado(Long simuladoId) {
        return simuladoRepository.findById(simuladoId)
                .orElseThrow(() -> new RuntimeException("Simulado nao encontrado"));
    }

    public Optional<Resposta> respostaDaQuestao(Long simuladoId, Long questaoId) {
        return respostaRepository.findBySimuladoIdAndQuestaoId(simuladoId, questaoId);
    }

    public long totalQuestoes(Long simuladoId) {
        return simuladoQuestaoRepository.countBySimuladoId(simuladoId);
    }

    public long totalRespondidas(Long simuladoId) {
        return respostaRepository.countBySimuladoId(simuladoId);
    }

    // Gera o resultado do simulado
    public ResultadoSimuladoDTO gerarResultado(Long simuladoId) {
        List<Resposta> respostas = respostaRepository.findBySimuladoId(simuladoId);

        int total = respostas.size();
        int acertos = (int) respostas.stream().filter(Resposta::isAcertou).count();
        int erros = total - acertos;
        double percentual = total == 0 ? 0 : (acertos * 100.0) / total;

        return new ResultadoSimuladoDTO(total, acertos, erros, percentual);
    }

}