package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Questao;
import java.util.List;

// Repositório de Questao
public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    // Busca as questoes de todas as provas de um curso.
    List<Questao> findByProvaCurso(String curso);
}