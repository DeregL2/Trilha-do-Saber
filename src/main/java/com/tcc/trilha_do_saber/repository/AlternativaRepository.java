package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Alternativa;
import java.util.List;


// Repositório de Alternativa.
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {

    List<Alternativa> findByQuestaoId(Long questaoId);

    List<Alternativa> findByQuestaoIdOrderByIdAsc(Long questaoId);
}
