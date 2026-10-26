package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tcc.trilha_do_saber.model.Simulado;
import java.util.List;

public interface SimuladoRepository extends JpaRepository<Simulado, Long> {

    List<Simulado> findByAlunoIdAndFinalizadoTrueOrderByDataFimAsc(Long alunoId);

    boolean existsByIdAndAlunoId(Long id, Long alunoId);

    List<Simulado> findByFinalizadoFalse();

    @Query("select s from Simulado s join fetch s.aluno a "
            + "where s.finalizado = true and a.ativo = true and a.anonimizado = false "
            + "and lower(a.curso) = lower(:curso)")
    List<Simulado> buscarFinalizadosDoCurso(@Param("curso") String curso);
}