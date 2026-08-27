package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Questao;

// Repositório de Questao. Já vem com save/findById/findAll/delete prontos.
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
}