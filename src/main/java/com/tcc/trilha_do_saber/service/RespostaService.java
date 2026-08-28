package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import com.tcc.trilha_do_saber.repository.RespostaRepository;
import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Resposta;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service // Anotação do Spring informando que essa Classe contem lógica de negocio e gerencia ela como um bean.
public class RespostaService {

    // Atributos. FINAL - Após o atributo ser construido nao pode mais ser alterado.
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final RespostaRepository respostaRepository;


    // Construtor da Classe
    public RespostaService(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, RespostaRepository respostaRepository){

        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.respostaRepository = respostaRepository;

    }

    //Metodo
    public Resposta responder(Long questaoId, Long alternativaId, String nomeUsuario){
        
        // Faz uma busca pelo ID no Banco de dados, se nao encotrar retorna o erro descrito
        Questao questao = questaoRepository.findById(questaoId).orElseThrow(() -> new RuntimeException("Questão não encontrada"));

        // Mesma coisa do de cima
        Alternativa alternativa = alternativaRepository.findById(alternativaId).orElseThrow(() -> new RuntimeException("Alternativa não encontrada"));

        boolean correto = alternativa.isCorreta();

        // Instancia o objeto passando os parametros declarados na Classe Resposta. 
        Resposta resp = new Resposta(nomeUsuario, questao, alternativa, correto, LocalDateTime.now());

        // Salva a construção da resposta no Banco e retorna os valores preenchidos
        return respostaRepository.save(resp);
    
    }
}
