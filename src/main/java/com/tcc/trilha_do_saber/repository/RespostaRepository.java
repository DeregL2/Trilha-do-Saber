package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Resposta;
import java.util.List;
import java.util.Optional;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    Optional<Resposta> findBySimuladoIdAndQuestaoId(Long simuladoId, Long questaoId);

    List<Resposta> findBySimuladoId(Long simuladoId);

    long countBySimuladoId(Long simuladoId);

    long countBySimuladoIdAndAcertouTrue(Long simuladoId);
}