package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.ProvaAplicadaTurma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvaAplicadaTurmaRepository extends JpaRepository<ProvaAplicadaTurma, Long> {

    List<ProvaAplicadaTurma> findByProvaAplicadaId(Long provaAplicadaId);

    List<ProvaAplicadaTurma> findByCursoIgnoreCaseAndSemestreAndTurmaIgnoreCaseAndTurnoIgnoreCase(
            String curso, int semestre, String turma, String turno);
}