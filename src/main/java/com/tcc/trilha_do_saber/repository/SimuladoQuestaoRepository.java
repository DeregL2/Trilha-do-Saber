package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.SimuladoQuestao;
import java.util.List;
import java.util.Optional;

// Repositorio das questoes sorteadas de cada simulado.
public interface SimuladoQuestaoRepository extends JpaRepository<SimuladoQuestao, Long> {

    // Todas as questoes de um simulado, na ordem do sorteio
    List<SimuladoQuestao> findBySimuladoIdOrderByOrdemAsc(Long simuladoId);

    // Uma questao pela posicao. Usado na URL /simulado/{id}/questao/{ordem}
    Optional<SimuladoQuestao> findBySimuladoIdAndOrdem(Long simuladoId, int ordem);

    // Confere se a questao realmente faz parte do simulado
    Optional<SimuladoQuestao> findBySimuladoIdAndQuestaoId(Long simuladoId, Long questaoId);

    long countBySimuladoId(Long simuladoId);
}