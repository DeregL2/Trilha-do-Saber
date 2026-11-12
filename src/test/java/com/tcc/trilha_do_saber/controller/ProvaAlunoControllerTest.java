package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.service.ProvaDoAlunoService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvaAlunoControllerTest {

    @Mock
    private ProvaDoAlunoService servico;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirect;

    @Test
    void quemNaoEAlunoVoltaParaOLoginENadaEIniciado() {
        when(session.getAttribute("usuarioLogado")).thenReturn(null);
        assertEquals("redirect:/login", controller().iniciar(3L, session, redirect));

        when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(5L, "Prof", "p@umc.br", "PROFESSOR"));
        assertEquals("redirect:/login", controller().iniciar(3L, session, redirect));

        verifyNoInteractions(servico);
    }

    @Test
    void iniciarLevaOAlunoParaAPrimeiraQuestao() {
        when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(1L, "Ana", "ana@umc.br", "ALUNO"));
        Simulado simulado = mock(Simulado.class);
        when(simulado.getId()).thenReturn(300L);
        when(servico.iniciar(3L, 1L)).thenReturn(simulado);

        assertEquals("redirect:/simulado/300/questao/1", controller().iniciar(3L, session, redirect));
    }

    @Test
    void recusaVoltaParaATelaDeEscolhaComOMotivo() {
        when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(1L, "Ana", "ana@umc.br", "ALUNO"));
        when(servico.iniciar(3L, 1L)).thenThrow(new IllegalArgumentException("A prova ainda não abriu"));

        assertEquals("redirect:/simulado/novo", controller().iniciar(3L, session, redirect));

        verify(redirect).addFlashAttribute("erroSimulado", "A prova ainda não abriu");
    }

    private ProvaAlunoController controller() {
        return new ProvaAlunoController(servico);
    }
}