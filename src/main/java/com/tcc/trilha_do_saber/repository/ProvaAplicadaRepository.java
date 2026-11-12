package com.tcc.trilha_do_saber.repository;

import com.tcc.trilha_do_saber.model.ProvaAplicada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvaAplicadaRepository extends JpaRepository<ProvaAplicada, Long> {

    List<ProvaAplicada> findByCriadorTipoAndCriadorIdOrderByDataAberturaDesc(String criadorTipo, Long criadorId);
}