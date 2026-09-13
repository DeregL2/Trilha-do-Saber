package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Professor;
import java.util.Optional;

// Repositório de Professor
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);
}
