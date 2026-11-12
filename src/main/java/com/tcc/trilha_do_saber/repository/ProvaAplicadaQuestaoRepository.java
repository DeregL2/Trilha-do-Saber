package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.ProvaAplicadaQuestao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvaAplicadaQuestaoRepository extends JpaRepository<ProvaAplicadaQuestao, Long> {

    List<ProvaAplicadaQuestao> findByProvaAplicadaIdOrderByOrdemAsc(Long provaAplicadaId);

    long countByProvaAplicadaId(Long provaAplicadaId);
}