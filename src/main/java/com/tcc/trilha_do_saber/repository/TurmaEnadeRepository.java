package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.TurmaEnade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaEnadeRepository extends JpaRepository<TurmaEnade, Long> {

    Optional<TurmaEnade> findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurno(
            String periodoLetivo, String curso, int semestre, String turma, String turno);
}