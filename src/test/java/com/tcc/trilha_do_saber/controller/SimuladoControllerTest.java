package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.exception.SemQuestoesException;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.service.SimuladoService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimuladoControllerTest {

    @Mock
    private SimuladoService simuladoService;

    @Mock
    private AlternativaRepository alternativaRepository;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private SimuladoController simuladoController;

    @Test
    void deveLevarOAlunoParaAPrimeiraQuestaoQuandoOSimuladoComeca() {
        when(session.getAttribute("usuarioLogado"))
                .thenReturn(new UsuarioSessaoDTO(1L, "Ana", "ana@umc.br", "ALUNO"));
        Simulado simulado = mock(Simulado.class);
        when(simulado.getId()).thenReturn(10L);
        when(simuladoService.iniciar(1L)).thenReturn(simulado);

        String destino = simuladoController.iniciar(session, redirectAttributes);

        assertEquals("redirect:/simulado/10/questao/1", destino);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void deveVoltarAoDashboardComMensagemQuandoOCursoNaoTemQuestoes() {
        when(session.getAttribute("usuarioLogado"))
                .thenReturn(new UsuarioSessaoDTO(1L, "Bruna", "bruna@umc.br", "ALUNO"));
        when(simuladoService.iniciar(1L)).thenThrow(new SemQuestoesException("Engenharia de Software"));

        String destino = simuladoController.iniciar(session, redirectAttributes);

        assertEquals("redirect:/aluno/dashboard", destino);
        ArgumentCaptor<Object> mensagem = ArgumentCaptor.forClass(Object.class);
        verify(redirectAttributes).addFlashAttribute(eq("erroSimulado"), mensagem.capture());
        assertTrue(mensagem.getValue().toString().contains("Engenharia de Software"));
    }

    @Test
    void deveMandarParaOLoginQuandoNaoHaAlunoLogado() {
        when(session.getAttribute("usuarioLogado")).thenReturn(null);

        String destino = simuladoController.iniciar(session, redirectAttributes);

        assertEquals("redirect:/login", destino);
        verifyNoInteractions(simuladoService);
    }
}