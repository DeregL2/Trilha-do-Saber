package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Tema;

// Repositório de Tema. Sem corpo — o Spring gera a implementação sozinho.
// <Tema, Long> = entidade gerenciada + tipo do id
public interface TemaRepository extends JpaRepository<Tema, Long> {
}