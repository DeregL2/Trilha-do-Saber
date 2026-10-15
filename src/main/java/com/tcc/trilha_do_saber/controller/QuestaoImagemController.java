package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// devolve a imagem recortada de uma questão, pra exibir no navegador ou na tela do aluno depois
@RestController
public class QuestaoImagemController {

    private final QuestaoRepository questaoRepository;

    public QuestaoImagemController(QuestaoRepository questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    @GetMapping("/questao/{id}/imagem")
    public ResponseEntity<byte[]> verImagem(@PathVariable Long id) {
        Optional<Questao> questao = questaoRepository.findById(id);

        if (questao.isEmpty() || questao.get().getImagem() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(questao.get().getImagem());
    }
}