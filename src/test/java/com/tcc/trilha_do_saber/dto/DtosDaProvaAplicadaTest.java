package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.StatusProva;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtosDaProvaAplicadaTest {

    @Test
    void turmaAlvoGeraChaveERotulo() {
        TurmaAlvoDTO turma = new TurmaAlvoDTO("Sistemas de Informação", 8, "B", "Noturno");

        assertEquals("Sistemas de Informação|8|B|Noturno", turma.getChave());
        assertEquals("Sistemas de Informação · 8º semestre · turma B · Noturno", turma.getRotulo());
    }

    @Test
    void turmaAlvoVoltaDaChave() {
        TurmaAlvoDTO turma = TurmaAlvoDTO.de("Sistemas de Informação|8|B|Noturno");

        assertEquals("Sistemas de Informação", turma.getCurso());
        assertEquals(8, turma.getSemestre());
        assertEquals("B", turma.getTurma());
        assertEquals("Noturno", turma.getTurno());
    }

    @Test
    void chaveInvalidaEhRecusada() {
        for (String ruim : new String[]{null, "", "a|b|c", "a|x|c|d", "a|8|c|d|e", "|||"}) {
            assertThrows(IllegalArgumentException.class, () -> TurmaAlvoDTO.de(ruim), String.valueOf(ruim));
        }
    }

    @Test
    void turmaAlvoIgnoraCaixaNaIgualdade() {
        TurmaAlvoDTO a = new TurmaAlvoDTO("Sistemas de Informação", 8, "B", "Noturno");
        TurmaAlvoDTO b = new TurmaAlvoDTO("sistemas de informação", 8, "b", "noturno");
        TurmaAlvoDTO outra = new TurmaAlvoDTO("Sistemas de Informação", 8, "C", "Noturno");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, outra);
        assertNotEquals(a, "texto");
    }

    @Test
    void questaoDoBancoSabeSeOResumoEOEnunciadoInteiro() {
        assertTrue(new QuestaoBancoDTO(1L, 3, "curto", "curto", "Fácil", false).isResumoCompleto());
        assertFalse(new QuestaoBancoDTO(1L, 3, "cur…", "curto longo", "Fácil", true).isResumoCompleto());
    }

    @Test
    void provaDoBancoTemRotuloComAnoECurso() {
        ProvaBancoDTO prova = new ProvaBancoDTO(9L, 2022, "Sistemas de Informação", List.of());

        assertEquals("ENADE 2022 · Sistemas de Informação", prova.getRotulo());
    }

    @Test
    void resumoSoEstaAgendadaNoStatusAgendada() {
        LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 19, 0);

        assertTrue(new ProvaAplicadaResumoDTO(1L, "P", agora, agora, StatusProva.AGENDADA, 5, List.of()).isAgendada());
        assertFalse(new ProvaAplicadaResumoDTO(1L, "P", agora, agora, StatusProva.ABERTA, 5, List.of()).isAgendada());
        assertFalse(new ProvaAplicadaResumoDTO(1L, "P", agora, agora, StatusProva.CANCELADA, 5, List.of()).isAgendada());
    }

    @Test
    void linhaDoAlunoCalculaOPercentualSobreOTotalDaProva() {
        LinhaProvaAlunoDTO linha = new LinhaProvaAlunoDTO("Ana", "RA-1", "8º", "Enviada", true, 8, 6, 10, null, false);

        assertEquals(60.0, linha.getPercentual(), 0.0001);
        assertEquals(0.0, new LinhaProvaAlunoDTO("Bia", "RA-2", "8º", "Não iniciou", false, 0, 0, 0, null, false).getPercentual(), 0.0001);
    }

    @Test
    void detalheSabeSeTemMediaETotalDeAlunos() {
        ProvaAplicadaResumoDTO resumo = new ProvaAplicadaResumoDTO(1L, "P", LocalDateTime.now(), LocalDateTime.now(),
                StatusProva.ABERTA, 5, List.of());
        LinhaProvaAlunoDTO linha = new LinhaProvaAlunoDTO("Ana", "RA-1", "8º", "Enviada", true, 5, 4, 5, null, false);

        DetalheProvaAplicadaDTO comMedia = new DetalheProvaAplicadaDTO(resumo, List.of(linha, linha), 2, 2, 0, 80.0);
        DetalheProvaAplicadaDTO semMedia = new DetalheProvaAplicadaDTO(resumo, List.of(), 0, 0, 0, null);

        assertTrue(comMedia.isTemMedia());
        assertEquals(2, comMedia.getTotalAlunos());
        assertFalse(semMedia.isTemMedia());
        assertEquals(0, semMedia.getTotalAlunos());
    }

    @Test
    void provaDisponivelSabeEmQualSituacaoEsta() {
        LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 19, 0);

        assertTrue(new ProvaDisponivelDTO(1L, "P", agora, agora, ProvaDisponivelDTO.Situacao.AGENDADA, null).isAgendada());
        assertTrue(new ProvaDisponivelDTO(1L, "P", agora, agora, ProvaDisponivelDTO.Situacao.ABERTA, null).isAberta());
        assertTrue(new ProvaDisponivelDTO(1L, "P", agora, agora, ProvaDisponivelDTO.Situacao.EM_ANDAMENTO, 7L).isEmAndamento());
        assertTrue(new ProvaDisponivelDTO(1L, "P", agora, agora, ProvaDisponivelDTO.Situacao.ENVIADA, 7L).isEnviada());
        assertFalse(new ProvaDisponivelDTO(1L, "P", agora, agora, ProvaDisponivelDTO.Situacao.ABERTA, null).isEmAndamento());
    }

    @Test
    void provaDisponivelCalculaOsSegundosAteAAberturaEAteOEncerramento() {
        LocalDateTime agora = LocalDateTime.now();
        ProvaDisponivelDTO futura = new ProvaDisponivelDTO(1L, "P", agora.plusSeconds(120), agora.plusSeconds(3600),
                ProvaDisponivelDTO.Situacao.AGENDADA, null);

        assertTrue(futura.getSegundosParaAbrir() >= 118 && futura.getSegundosParaAbrir() <= 120, "abrir: " + futura.getSegundosParaAbrir());
        assertTrue(futura.getSegundosParaEncerrar() >= 3598 && futura.getSegundosParaEncerrar() <= 3600, "encerrar: " + futura.getSegundosParaEncerrar());
    }

    @Test
    void segundosNuncaFicamNegativosDepoisDoHorario() {
        LocalDateTime agora = LocalDateTime.now();
        ProvaDisponivelDTO passada = new ProvaDisponivelDTO(1L, "P", agora.minusHours(2), agora.minusHours(1),
                ProvaDisponivelDTO.Situacao.ENVIADA, 7L);

        assertEquals(0, passada.getSegundosParaAbrir());
        assertEquals(0, passada.getSegundosParaEncerrar());
    }

    @Test
    void turmaComAlunosContaOsAlunosDaTurma() {
        TurmaAlvoDTO turma = new TurmaAlvoDTO("Sistemas de Informação", 8, "B", "Noturno");
        TurmaComAlunosDTO grupo = new TurmaComAlunosDTO(turma, List.of(new AlunoOpcaoDTO(1L, "Ana", "RA-1"), new AlunoOpcaoDTO(2L, "Bia", "RA-2")));

        assertEquals(2, grupo.getTotalAlunos());
        assertEquals(turma, grupo.getTurma());
        assertEquals("Ana", grupo.getAlunos().get(0).getNome());
        assertEquals("RA-2", grupo.getAlunos().get(1).getRegistro());
        assertEquals(0, new TurmaComAlunosDTO(turma, List.of()).getTotalAlunos());
    }

    @Test
    void modeloDeProvaGuardaTituloEQuestoes() {
        ModeloProvaDTO modelo = new ModeloProvaDTO("Prova X", List.of(3L, 1L));

        assertEquals("Prova X", modelo.getTitulo());
        assertEquals(List.of(3L, 1L), modelo.getQuestaoIds());
    }

    @Test
    void desempenhoDaQuestaoCalculaOPercentualEONivel() {
        assertEquals(80.0, new DesempenhoQuestaoDTO(1, 12, "r", 4, 5).getPercentual(), 0.0001);
        assertEquals(0.0, new DesempenhoQuestaoDTO(1, 12, "r", 0, 0).getPercentual(), 0.0001);
        assertEquals("facil", new DesempenhoQuestaoDTO(1, 12, "r", 7, 10).getNivel());
        assertEquals("medio", new DesempenhoQuestaoDTO(1, 12, "r", 69, 100).getNivel());
        assertEquals("medio", new DesempenhoQuestaoDTO(1, 12, "r", 4, 10).getNivel());
        assertEquals("dificil", new DesempenhoQuestaoDTO(1, 12, "r", 39, 100).getNivel());
        assertEquals("dificil", new DesempenhoQuestaoDTO(1, 12, "r", 0, 0).getNivel());
    }

    @Test
    void faixaDeNotaGuardaRotuloEQuantidade() {
        FaixaNotaDTO faixa = new FaixaNotaDTO("0–20%", 3);

        assertEquals("0–20%", faixa.getRotulo());
        assertEquals(3, faixa.getQuantidade());
    }

    @Test
    void detalheComDashboardExpoeMaiorMenorFaixasEQuestoes() {
        ProvaAplicadaResumoDTO resumo = new ProvaAplicadaResumoDTO(1L, "P", LocalDateTime.now(), LocalDateTime.now(),
                StatusProva.ABERTA, 5, List.of());

        DetalheProvaAplicadaDTO detalhe = new DetalheProvaAplicadaDTO(resumo, List.of(), 3, 2, 0, 70.0, 90.0, 50.0,
                List.of(new FaixaNotaDTO("0–20%", 0), new FaixaNotaDTO("20–40%", 4), new FaixaNotaDTO("40–60%", 1)),
                List.of(new DesempenhoQuestaoDTO(1, 3, "r", 2, 3)));

        assertEquals(90.0, detalhe.getMaiorNota(), 0.0001);
        assertEquals(50.0, detalhe.getMenorNota(), 0.0001);
        assertEquals(3, detalhe.getFaixas().size());
        assertEquals(4, detalhe.getMaiorFaixa());
        assertEquals(1, detalhe.getQuestoes().size());
    }

    @Test
    void detalheSemDashboardTemListasVaziasEMaiorFaixaZero() {
        ProvaAplicadaResumoDTO resumo = new ProvaAplicadaResumoDTO(1L, "P", LocalDateTime.now(), LocalDateTime.now(),
                StatusProva.ABERTA, 5, List.of());

        DetalheProvaAplicadaDTO detalhe = new DetalheProvaAplicadaDTO(resumo, List.of(), 0, 0, 0, null);

        assertEquals(0, detalhe.getMaiorFaixa());
        assertEquals(0, detalhe.getFaixas().size());
        assertEquals(0, detalhe.getQuestoes().size());
        org.junit.jupiter.api.Assertions.assertNull(detalhe.getMaiorNota());
        org.junit.jupiter.api.Assertions.assertNull(detalhe.getMenorNota());
    }
}