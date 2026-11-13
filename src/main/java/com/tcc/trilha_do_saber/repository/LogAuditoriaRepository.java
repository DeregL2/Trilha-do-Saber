package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.LogAuditoria;
import java.time.LocalDateTime;
import java.util.List;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findAllByOrderByDataHoraDesc();

    List<LogAuditoria> findByDataHoraBefore(LocalDateTime limite);

    List<LogAuditoria> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    List<LogAuditoria> findByUsuarioTipoAndUsuarioIdOrderByDataHoraDesc(String usuarioTipo, Long usuarioId);

    List<LogAuditoria> findByUsuarioTipoAndUsuarioNomeIgnoreCaseOrderByDataHoraDesc(String usuarioTipo, String usuarioNome);

    List<LogAuditoria> findByEntidadeTipoAndEntidadeIdOrderByDataHoraDesc(String entidadeTipo, Long entidadeId);
}