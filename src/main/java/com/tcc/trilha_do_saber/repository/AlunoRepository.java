package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Aluno;
import java.util.Optional;

// Repositório de Aluno. Sem corpo — o Spring gera a implementação sozinho.
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Vai usar pra checar se o email ja esta cadastrado antes de criar
    Optional<Aluno> findByEmail(String email);
}
