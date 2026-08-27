package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Alternativa;

// Repositório de Alternativa.
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {
}