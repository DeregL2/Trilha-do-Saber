package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.ProvaAplicadaAluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvaAplicadaAlunoRepository extends JpaRepository<ProvaAplicadaAluno, Long> {

    List<ProvaAplicadaAluno> findByProvaAplicadaId(Long provaAplicadaId);

    List<ProvaAplicadaAluno> findByAlunoId(Long alunoId);

    long countByProvaAplicadaId(Long provaAplicadaId);

    boolean existsByProvaAplicadaIdAndAlunoId(Long provaAplicadaId, Long alunoId);
}