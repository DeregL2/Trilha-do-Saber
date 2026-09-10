package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Simulado;

// Repositorio de Simulado. Ja vem com save/findById/findAll/delete prontos.
public interface SimuladoRepository extends JpaRepository<Simulado, Long> {
}