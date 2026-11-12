package com.tcc.trilha_do_saber.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProvaAplicadaTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 11, 13, 19, 30);
    private static final LocalDateTime ENCERRAMENTO = LocalDateTime.of(2026, 11, 13, 21, 0);

    private ProvaAplicada prova() {
        return new ProvaAplicada("Prova ENADE", "PROFESSOR", 5L, "Carlos", ABERTURA, ENCERRAMENTO, ABERTURA.minusDays(2));
    }

    @Test
    void antesDaAberturaEstaAgendada() {
        assertEquals(StatusProva.AGENDADA, prova().statusEm(ABERTURA.minusSeconds(1)));
    }

    @Test
    void naAberturaEAteOEncerramentoEstaAberta() {
        assertEquals(StatusProva.ABERTA, prova().statusEm(ABERTURA));
        assertEquals(StatusProva.ABERTA, prova().statusEm(ENCERRAMENTO.minusSeconds(1)));
    }

    @Test
    void noEncerramentoEDepoisEstaEncerrada() {
        assertEquals(StatusProva.ENCERRADA, prova().statusEm(ENCERRAMENTO));
        assertEquals(StatusProva.ENCERRADA, prova().statusEm(ENCERRAMENTO.plusDays(1)));
    }

    @Test
    void provaCanceladaFicaCanceladaEmQualquerHorario() {
        ProvaAplicada prova = prova();

        assertFalse(prova.isCancelada());
        prova.cancelar();

        assertTrue(prova.isCancelada());
        assertEquals(StatusProva.CANCELADA, prova.statusEm(ABERTURA.minusDays(1)));
        assertEquals(StatusProva.CANCELADA, prova.statusEm(ABERTURA.plusMinutes(10)));
        assertEquals(StatusProva.CANCELADA, prova.statusEm(ENCERRAMENTO.plusDays(1)));
    }

    @Test
    void osDadosDeCriacaoSaoGuardados() {
        ProvaAplicada prova = prova();

        assertEquals("Prova ENADE", prova.getTitulo());
        assertEquals("PROFESSOR", prova.getCriadorTipo());
        assertEquals(5L, prova.getCriadorId());
        assertEquals("Carlos", prova.getCriadorNome());
        assertEquals(ABERTURA, prova.getDataAbertura());
        assertEquals(ENCERRAMENTO, prova.getDataEncerramento());
        assertEquals(ABERTURA.minusDays(2), prova.getCriadaEm());
    }

    @Test
    void osRotulosDosStatusSaoEmPortugues() {
        assertEquals("Agendada", StatusProva.AGENDADA.getRotulo());
        assertEquals("Aberta", StatusProva.ABERTA.getRotulo());
        assertEquals("Encerrada", StatusProva.ENCERRADA.getRotulo());
        assertEquals("Cancelada", StatusProva.CANCELADA.getRotulo());
    }

    @Test
    void gabaritoNaoEReditoPorPadraoEPodeSerRetido() {
        ProvaAplicada prova = prova();

        assertFalse(prova.isGabaritoRetido());
        prova.definirGabaritoRetido(true);
        assertTrue(prova.isGabaritoRetido());
        prova.definirGabaritoRetido(false);
        assertFalse(prova.isGabaritoRetido());
    }
}