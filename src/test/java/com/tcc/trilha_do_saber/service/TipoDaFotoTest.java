package com.tcc.trilha_do_saber.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// A foto de perfil é aceita pelo conteúdo do arquivo, não pelo tipo que o navegador informa
class TipoDaFotoTest {

    @Test
    void reconhecePngJpegEWebp() {
        assertEquals("image/png", MinhaContaService.tipoDaImagem(new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A}));
        assertEquals("image/jpeg", MinhaContaService.tipoDaImagem(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0}));
        assertEquals("image/webp", MinhaContaService.tipoDaImagem("RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    void recusaSvgETextoMesmoQueONavegadorDigaQueEImagem() {
        assertNull(MinhaContaService.tipoDaImagem("<svg xmlns=\"http://www.w3.org/2000/svg\"><script/></svg>".getBytes(StandardCharsets.UTF_8)));
        assertNull(MinhaContaService.tipoDaImagem("<html><body>oi</body></html>".getBytes(StandardCharsets.UTF_8)));
        assertNull(MinhaContaService.tipoDaImagem(new byte[0]));
        assertNull(MinhaContaService.tipoDaImagem(null));
    }
}
