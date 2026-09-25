package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.LogAuditoria;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogAuditoriaService {


    private static final int DIAS_RETENCAO = 365;

    private final LogAuditoriaRepository logAuditoriaRepository;

    public LogAuditoriaService(LogAuditoriaRepository logAuditoriaRepository){
        this.logAuditoriaRepository = logAuditoriaRepository;
    }


    public void registrar(Long usuarioId, String usuarioNome, String usuarioTipo,
                          TipoAcao acao, String entidadeTipo, Long entidadeId, String detalhes,
                          String ip, Long duracaoMs){

        LogAuditoria log = new LogAuditoria(usuarioId, usuarioNome, usuarioTipo, acao,
                entidadeTipo, entidadeId, detalhes, ip, duracaoMs);

        log.setHashIntegridade(calcularHash(log.montarConteudoParaHash()));

        logAuditoriaRepository.save(log);
    }


    public void registrarSemAtor(String usuarioNomeTentado, TipoAcao acao, String entidadeTipo,
                                 String detalhes, String ip, Long duracaoMs){

        LogAuditoria log = new LogAuditoria(null, usuarioNomeTentado, "DESCONHECIDO", acao,
                entidadeTipo, null, detalhes, ip, duracaoMs);

        log.setHashIntegridade(calcularHash(log.montarConteudoParaHash()));

        logAuditoriaRepository.save(log);
    }


    public boolean verificarIntegridade(LogAuditoria log){
        String hashRecalculado = calcularHash(log.montarConteudoParaHash());
        return hashRecalculado.equals(log.getHashIntegridade());
    }

    private String calcularHash(String conteudo){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = digest.digest(conteudo.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : bytesHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de hash indisponivel", e);
        }
    }

    public List<LogAuditoria> listarTodos(){
        return logAuditoriaRepository.findAllByOrderByDataHoraDesc();
    }

    public int expurgarLogsAntigos(){
        LocalDateTime limite = LocalDateTime.now().minusDays(DIAS_RETENCAO);
        List<LogAuditoria> antigos = logAuditoriaRepository.findByDataHoraBefore(limite);
        logAuditoriaRepository.deleteAll(antigos);
        return antigos.size();
    }
}