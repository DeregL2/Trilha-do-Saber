package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.service.ProvaDoAlunoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvaEmAndamentoInterceptorTest {

    @Mock
    private ProvaDoAlunoService provaDoAlunoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ProvaEmAndamentoInterceptor interceptor;

    @BeforeEach
    void preparar() {
        interceptor = new ProvaEmAndamentoInterceptor(provaDoAlunoService);
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("");
        lenient().when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(1L, "Ana", "ana@umc.br", "ALUNO"));
        lenient().when(provaDoAlunoService.simuladoAplicadoEmAndamento(1L)).thenReturn(Optional.of(10L));
    }

    @Test
    void semSessaoOuSemUsuarioLogadoNadaEBloqueado() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        assertTrue(passa("/aluno/dashboard"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioLogado")).thenReturn(null);
        assertTrue(passa("/aluno/dashboard"));

        verifyNoInteractions(provaDoAlunoService);
    }

    @Test
    void quemNaoEAlunoNuncaEBloqueado() throws Exception {
        for (String tipo : new String[]{"PROFESSOR", "COORDENADOR", "ADMIN"}) {
            when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(2L, "X", "x@umc.br", tipo));

            assertTrue(passa("/conta"), tipo);
        }
        verifyNoInteractions(provaDoAlunoService);
    }

    @Test
    void alunoSemProvaEmAndamentoNavegaLivremente() throws Exception {
        when(provaDoAlunoService.simuladoAplicadoEmAndamento(1L)).thenReturn(Optional.empty());

        assertTrue(passa("/aluno/dashboard"));
        assertTrue(passa("/simulado/novo"));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void alunoEmProvaEhDevolvidoParaAProvaEmQualquerOutraPagina() throws Exception {
        for (String caminho : new String[]{"/aluno/dashboard", "/aluno/inicio", "/conta", "/simulado/novo", "/simulado/iniciar",
                "/simulado/prova/3/iniciar", "/aluno/ranking", "/simulado/11/questao/1", "/simulado/100/questao/1", "/simulado/1/resultado"}) {
            assertFalse(passa(caminho), caminho);
        }
        verify(response, times(10)).sendRedirect("/simulado/10/questao/1");
    }

    @Test
    void alunoEmProvaPodeUsarTodasAsRotasDaPropriaProva() throws Exception {
        for (String caminho : new String[]{"/simulado/10/questao/1", "/simulado/10/questao/7", "/simulado/10/questao/7/salvar",
                "/simulado/10/saida", "/simulado/10/resultado", "/simulado/10/concluido"}) {
            assertTrue(passa(caminho), caminho);
        }
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void arquivosEstaticosImagensDeQuestaoELogoutSaoLiberadosSemConsultarOBanco() throws Exception {
        for (String caminho : new String[]{"/css/simulado.css", "/css/img/icons/img/logo.png", "/js/app.js", "/img/x.png",
                "/images/x.png", "/favicon.ico", "/error", "/logout", "/questao/55/imagem"}) {
            assertTrue(passa(caminho), caminho);
        }
        verifyNoInteractions(provaDoAlunoService);
    }

    @Test
    void imagemDeQuestaoSoELiberadaNoFormatoCerto() throws Exception {
        assertFalse(passa("/questao/abc/imagem"));
        assertFalse(passa("/questao/55/imagem/extra"));
        assertFalse(passa("/questao/55"));
    }

    @Test
    void ORedirecionamentoRespeitaOContextoDaAplicacao() throws Exception {
        when(request.getContextPath()).thenReturn("/trilha");
        when(request.getRequestURI()).thenReturn("/trilha/aluno/dashboard");

        assertFalse(interceptor.preHandle(request, response, new Object()));

        verify(response).sendRedirect("/trilha/simulado/10/questao/1");
    }

    @Test
    void comContextoAsRotasDaPropriaProvaContinuamLiberadas() throws Exception {
        when(request.getContextPath()).thenReturn("/trilha");
        when(request.getRequestURI()).thenReturn("/trilha/simulado/10/questao/2");

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void aListaDeCaminhosLiberadosEstaCerta() {
        assertTrue(ProvaEmAndamentoInterceptor.liberado("/logout"));
        assertTrue(ProvaEmAndamentoInterceptor.liberado("/css/a.css"));
        assertFalse(ProvaEmAndamentoInterceptor.liberado("/cssx/a.css"));
        assertFalse(ProvaEmAndamentoInterceptor.liberado("/aluno/dashboard"));
        assertFalse(ProvaEmAndamentoInterceptor.liberado("/login"));
    }

    private boolean passa(String caminho) throws Exception {
        lenient().when(request.getRequestURI()).thenReturn(caminho);
        return interceptor.preHandle(request, response, new Object());
    }
}