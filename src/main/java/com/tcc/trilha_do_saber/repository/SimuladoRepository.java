package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Simulado;
import java.util.List;

public interface SimuladoRepository extends JpaRepository<Simulado, Long> {

    List<Simulado> findByAlunoIdAndFinalizadoTrueOrderByDataFimAsc(Long alunoId);

    boolean existsByIdAndAlunoId(Long id, Long alunoId);
}