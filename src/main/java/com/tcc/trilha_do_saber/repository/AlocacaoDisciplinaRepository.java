package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.AlocacaoDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AlocacaoDisciplinaRepository extends JpaRepository<AlocacaoDisciplina, Long> {

    Optional<AlocacaoDisciplina> findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurnoAndDisciplinaIgnoreCase(
            String periodoLetivo, String curso, int semestre, String turma, String turno, String disciplina);

    List<AlocacaoDisciplina> findByPeriodoLetivoAndCursoInOrderByCursoAscSemestreAscTurmaAscDisciplinaAsc(
            String periodoLetivo, Collection<String> cursos);

    @Query("select distinct a.periodoLetivo from AlocacaoDisciplina a where a.curso in :cursos order by a.periodoLetivo desc")
    List<String> periodosDosCursos(@Param("cursos") Collection<String> cursos);
}