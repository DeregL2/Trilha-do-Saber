package com.tcc.trilha_do_saber.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;


    private Long usuarioId;
    private String usuarioNome;
    private String usuarioTipo; // ALUNO, PROFESSOR, COORDENADOR, ADMIN ou DESCONHECIDO

    @Enumerated(EnumType.STRING)
    private TipoAcao acao;

    private String entidadeTipo; // Aluno, Professor, Coordenador, Login...
    private Long entidadeId;

    @Column(columnDefinition = "TEXT")
    private String detalhes; // nunca guarda senha, nem antiga nem nova


    private String ip;

    private Long duracaoMs;

    @Column(length = 64)
    private String hashIntegridade;

    public LogAuditoria(){
    }

    public LogAuditoria(Long usuarioId, String usuarioNome, String usuarioTipo,
                        TipoAcao acao, String entidadeTipo, Long entidadeId, String detalhes,
                        String ip, Long duracaoMs){
        this.dataHora = LocalDateTime.now();
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.usuarioTipo = usuarioTipo;
        this.acao = acao;
        this.entidadeTipo = entidadeTipo;
        this.entidadeId = entidadeId;
        this.detalhes = detalhes;
        this.ip = ip;
        this.duracaoMs = duracaoMs;
    }


    public String montarConteudoParaHash(){
        return (dataHora == null ? "" : dataHora.truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString())
                + "|" + (usuarioId == null ? "" : usuarioId)
                + "|" + (usuarioNome == null ? "" : usuarioNome)
                + "|" + (usuarioTipo == null ? "" : usuarioTipo)
                + "|" + (acao == null ? "" : acao)
                + "|" + (entidadeTipo == null ? "" : entidadeTipo)
                + "|" + (entidadeId == null ? "" : entidadeId)
                + "|" + (detalhes == null ? "" : detalhes)
                + "|" + (ip == null ? "" : ip)
                + "|" + (duracaoMs == null ? "" : duracaoMs);
    }

    public Long getId(){ return id; }
    public LocalDateTime getDataHora(){ return dataHora; }
    public Long getUsuarioId(){ return usuarioId; }
    public String getUsuarioNome(){ return usuarioNome; }
    public String getUsuarioTipo(){ return usuarioTipo; }
    public TipoAcao getAcao(){ return acao; }
    public String getEntidadeTipo(){ return entidadeTipo; }
    public Long getEntidadeId(){ return entidadeId; }
    public String getDetalhes(){ return detalhes; }
    public String getIp(){ return ip; }
    public Long getDuracaoMs(){ return duracaoMs; }
    public String getHashIntegridade(){ return hashIntegridade; }
    public void setHashIntegridade(String hashIntegridade){ this.hashIntegridade = hashIntegridade; }
}