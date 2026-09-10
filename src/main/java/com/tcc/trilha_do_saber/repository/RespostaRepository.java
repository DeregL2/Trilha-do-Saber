package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Resposta;
import java.util.List;
import java.util.Optional;

// Repositório de Resposta — onde vamos salvar cada tentativa de resposta do usuário.
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    // Busca a resposta ja dada para uma questao dentro de um simulado e troca a alternativa.
    Optional<Resposta> findBySimuladoIdAndQuestaoId(Long simuladoId, Long questaoId);

    List<Resposta> findBySimuladoId(Long simuladoId);

    long countBySimuladoId(Long simuladoId);
}