package com.tcc.trilha_do_saber;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FusoHorarioDaAplicacaoTest {

    @Test
    void oFusoHorarioDaAplicacaoEOdeBrasilia() {
        TimeZone original = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            TrilhaDoSaberApplication.configurarFusoHorario();

            assertEquals("America/Sao_Paulo", TimeZone.getDefault().getID());
            assertEquals(ZoneId.of("America/Sao_Paulo"), ZoneId.systemDefault());
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    void aConstanteDoFusoApontaParaSaoPaulo() {
        assertEquals("America/Sao_Paulo", TrilhaDoSaberApplication.FUSO_HORARIO);
    }
}