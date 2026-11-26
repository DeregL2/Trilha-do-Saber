package com.tcc.trilha_do_saber.controller;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContagemEnadeTest {

    @Test
    void antesDoDiaPadraoContaAteEsteAno() {
        assertEquals(LocalDate.of(2026, 11, 29), HomeController.proximoEnade(LocalDate.of(2026, 11, 25), null));
    }

    @Test
    void noProprioDiaAindaMostraZeroDias() {
        assertEquals(LocalDate.of(2026, 11, 29), HomeController.proximoEnade(LocalDate.of(2026, 11, 29), ""));
    }

    @Test
    void depoisDoDiaPadraoPassaParaOProximoAnoSemFicarNegativo() {
        assertEquals(LocalDate.of(2027, 11, 29), HomeController.proximoEnade(LocalDate.of(2026, 11, 30), null));
    }

    @Test
    void usaADataConfiguradaEnquantoElaNaoPassou() {
        assertEquals(LocalDate.of(2026, 11, 22), HomeController.proximoEnade(LocalDate.of(2026, 11, 1), "2026-11-22"));
    }

    @Test
    void dataConfiguradaJaPassadaOuInvalidaVoltaParaOPadrao() {
        assertEquals(LocalDate.of(2027, 11, 29), HomeController.proximoEnade(LocalDate.of(2026, 12, 1), "2026-11-22"));
        assertEquals(LocalDate.of(2026, 11, 29), HomeController.proximoEnade(LocalDate.of(2026, 11, 1), "22/11/2026"));
    }
}
