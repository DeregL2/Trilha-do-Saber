package com.tcc.trilha_do_saber.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimuladoAplicadoTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 11, 13, 19, 30);
    private static final LocalDateTime ENCERRAMENTO = LocalDateTime.of(2026, 11, 13, 21, 0);

    private Aluno aluno() {
        return new Aluno("Ana", "ana@umc.br", "hash", "RA-1", "Sistemas de Informação", 8, "B", "Noturno");
    }

    private ProvaAplicada prova() {
        return new ProvaAplicada("Prova ENADE", "PROFESSOR", 5L, "Carlos", ABERTURA, ENCERRAMENTO, ABERTURA.minusDays(1));
    }

    @Test
    void tentativaDeProvaAplicadaNasceEmModoProvaLigadaAProva() {
        ProvaAplicada prova = prova();

        Simulado simulado = new Simulado(aluno(), prova, ABERTURA.plusMinutes(10));

        assertTrue(simulado.isAplicado());
        assertSame(prova, simulado.getProvaAplicada());
        assertTrue(simulado.isModoProva());
        assertEquals("Prova ENADE", simulado.getTitulo());
        assertFalse(simulado.isFinalizado());
        assertFalse(simulado.isEncerradoPorSaida());
    }

    @Test
    void oLimiteEOHorarioDeEncerramentoParaTodosIndependenteDeQuandoEntrou() {
        Simulado cedo = new Simulado(aluno(), prova(), ABERTURA);
        Simulado tarde = new Simulado(aluno(), prova(), ABERTURA.plusMinutes(45));

        assertEquals(ENCERRAMENTO, cedo.getDataLimite());
        assertEquals(ENCERRAMENTO, tarde.getDataLimite());
    }

    @Test
    void aDuracaoEOTempoQueRestavaQuandoOAlunoEntrou() {
        assertEquals(90, new Simulado(aluno(), prova(), ABERTURA).getDuracaoMinutos());
        assertEquals(45, new Simulado(aluno(), prova(), ABERTURA.plusMinutes(45)).getDuracaoMinutos());
    }

    @Test
    void aDuracaoArredondaParaCimaEFicaPeloMenosUmMinuto() {
        assertEquals(2, new Simulado(aluno(), prova(), ENCERRAMENTO.minusSeconds(61)).getDuracaoMinutos());
        assertEquals(1, new Simulado(aluno(), prova(), ENCERRAMENTO.minusSeconds(30)).getDuracaoMinutos());
        assertEquals(1, new Simulado(aluno(), prova(), ENCERRAMENTO.minusSeconds(60)).getDuracaoMinutos());
        assertEquals(1, new Simulado(aluno(), prova(), ENCERRAMENTO.plusMinutes(5)).getDuracaoMinutos());
    }

    @Test
    void aTentativaExpiraSoDepoisDoEncerramento() {
        Simulado simulado = new Simulado(aluno(), prova(), ABERTURA.plusMinutes(10));

        assertFalse(simulado.expirou(ENCERRAMENTO));
        assertTrue(simulado.expirou(ENCERRAMENTO.plusSeconds(1)));
        assertTrue(simulado.estaEmAndamento(ENCERRAMENTO.minusMinutes(1)));
        assertFalse(simulado.estaEmAndamento(ENCERRAMENTO.plusMinutes(1)));
    }

    @Test
    void finalizarPorTempoUsaOHorarioDeEncerramento() {
        Simulado simulado = new Simulado(aluno(), prova(), ABERTURA.plusMinutes(10));

        simulado.finalizarPorTempo();

        assertEquals(ENCERRAMENTO, simulado.getDataFim());
        assertTrue(simulado.isFinalizadoPorTempo());
    }

    @Test
    void encerrarPorSaidaMarcaOMotivoENaoContaComoPorTempo() {
        Simulado simulado = new Simulado(aluno(), prova(), ABERTURA.plusMinutes(10));

        simulado.marcarEncerradoPorSaida();
        simulado.finalizar(ABERTURA.plusMinutes(20));

        assertTrue(simulado.isEncerradoPorSaida());
        assertTrue(simulado.isFinalizado());
        assertFalse(simulado.isFinalizadoPorTempo());
    }

    @Test
    void simuladoComumContinuaComLimitePelaDuracaoESemProvaAplicada() {
        Simulado simulado = new Simulado(aluno(), "Treino", 60, ABERTURA, true);

        assertFalse(simulado.isAplicado());
        assertEquals(ABERTURA.plusMinutes(60), simulado.getDataLimite());
        assertFalse(simulado.isEncerradoPorSaida());
    }

    @Test
    void gabaritoRetidoSegueAProvaESimuladoComumNuncaRetem() {
        ProvaAplicada prova = prova();
        Simulado simulado = new Simulado(aluno(), prova, ABERTURA);

        assertFalse(simulado.isGabaritoRetido());
        prova.definirGabaritoRetido(true);
        assertTrue(simulado.isGabaritoRetido());
        assertFalse(new Simulado(aluno(), "Treino", 60, ABERTURA, false).isGabaritoRetido());
    }
}