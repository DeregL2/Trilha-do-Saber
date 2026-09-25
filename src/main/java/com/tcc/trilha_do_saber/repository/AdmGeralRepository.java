package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.AdmGeral;
import java.util.Optional;

public interface AdmGeralRepository extends JpaRepository<AdmGeral, Long> {
    Optional<AdmGeral> findByEmail(String email);
}
