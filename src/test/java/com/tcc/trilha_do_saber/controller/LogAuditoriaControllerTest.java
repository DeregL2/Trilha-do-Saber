package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.AtividadeUsuarioDTO;
import com.tcc.trilha_do_saber.dto.FiltroLogDTO;
import com.tcc.trilha_do_saber.dto.LogAuditoriaViewDTO;
import com.tcc.trilha_do_saber.dto.ResultadoBuscaLogDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.LogAuditoria;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.service.LogAuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogAuditoriaControllerTest {

    @Mock
    private LogAuditoriaService servico;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private LogAuditoriaController controller;

    @BeforeEach
    void preparar() {
        controller = new LogAuditoriaController(servico);
        lenient().when(session.getAttribute("usuarioLogado")).thenReturn(new UsuarioSessaoDTO(1L, "Adm", "adm@umc.br", "ADMIN"));
        lenient().when(servico.buscar(any(FiltroLogDTO.class))).thenReturn(new ResultadoBuscaLogDTO(List.of(), 0));
    }

    @Test
    void quemNaoEAdminNaoVeNemOsLogsNemAAtividadeDeUsuario() {
        for (Object intruso : new Object[]{null, new UsuarioSessaoDTO(2L, "Ana", "a@umc.br", "ALUNO"),
                new UsuarioSessaoDTO(3L, "Coord", "c@umc.br", "COORDENADOR"), "texto"}) {
            when(session.getAttribute("usuarioLogado")).thenReturn(intruso);

            assertEquals("redirect:/login", controller.listar(null, null, null, null, null, model, session));
            assertEquals("redirect:/login", controller.usuario("ALUNO", 7L, null, model, session));
        }
        verifyNoInteractions(servico);
    }

    @Test
    void semFiltrosMostraTudoENaoMarcaFiltroAtivo() {
        String tela = controller.listar(null, null, null, null, null, model, session);

        assertEquals("admin/logs", tela);
        ArgumentCaptor<FiltroLogDTO> filtro = ArgumentCaptor.forClass(FiltroLogDTO.class);
        verify(servico).buscar(filtro.capture());
        assertFalse(filtro.getValue().temFiltro());
        verify(model).addAttribute("temFiltro", false);
        verify(model).addAttribute("totalEncontrado", 0);
        verify(model).addAttribute("cortado", false);
        verify(model).addAttribute("maximoLinhas", 500);
        verify(model).addAttribute("perfis", List.of("ALUNO", "PROFESSOR", "COORDENADOR", "ADMIN", "DESCONHECIDO"));
        verify(model).addAttribute("acoes", TipoAcao.values());
        verify(model).addAttribute("erroFiltro", (Object) null);
    }

    @Test
    void leOsFiltrosDaUrlLimpandoEspacosEPadronizandoOPerfil() {
        controller.listar("  ana lima ", "aluno", "login_falha", "2026-11-10", "2026-11-12", model, session);

        ArgumentCaptor<FiltroLogDTO> filtro = ArgumentCaptor.forClass(FiltroLogDTO.class);
        verify(servico).buscar(filtro.capture());
        assertEquals("ana lima", filtro.getValue().getUsuario());
        assertEquals("ALUNO", filtro.getValue().getPerfil());
        assertEquals(TipoAcao.LOGIN_FALHA, filtro.getValue().getAcao());
        assertEquals(LocalDate.of(2026, 11, 10), filtro.getValue().getDe());
        assertEquals(LocalDate.of(2026, 11, 12), filtro.getValue().getAte());
        verify(model).addAttribute("temFiltro", true);
        verify(model).addAttribute("filtroUsuario", "ana lima");
        verify(model).addAttribute("filtroPerfil", "ALUNO");
        verify(model).addAttribute("filtroAcao", "LOGIN_FALHA");
        verify(model).addAttribute("filtroDe", "2026-11-10");
        verify(model).addAttribute("filtroAte", "2026-11-12");
    }

    @Test
    void perfilEAcaoInventadosSaoIgnoradosEmVezDeQuebrar() {
        controller.listar(null, "HACKER", "NAO_EXISTE", null, null, model, session);

        ArgumentCaptor<FiltroLogDTO> filtro = ArgumentCaptor.forClass(FiltroLogDTO.class);
        verify(servico).buscar(filtro.capture());
        assertEquals("", filtro.getValue().getPerfil());
        assertNull(filtro.getValue().getAcao());
        verify(model).addAttribute("filtroPerfil", "");
        verify(model).addAttribute("filtroAcao", "");
        verify(model).addAttribute("temFiltro", false);
    }

    @Test
    void dataInvalidaEIgnoradaComAvisoENaoDerrubaAPagina() {
        String tela = controller.listar(null, null, null, "13/11/2026", "lixo", model, session);

        assertEquals("admin/logs", tela);
        ArgumentCaptor<FiltroLogDTO> filtro = ArgumentCaptor.forClass(FiltroLogDTO.class);
        verify(servico).buscar(filtro.capture());
        assertNull(filtro.getValue().getDe());
        assertNull(filtro.getValue().getAte());
        verify(model).addAttribute("erroFiltro", "Data inválida no filtro. Ela foi ignorada.");
        verify(model).addAttribute("filtroDe", "");
    }

    @Test
    void dataEmBrancoNaoGeraAvisoDeErro() {
        controller.listar(null, null, null, "", "  ", model, session);

        verify(model).addAttribute("erroFiltro", (Object) null);
    }

    @Test
    void informaOTotalEncontradoESeAListaFoiCortada() {
        LogAuditoria log = new LogAuditoria(1L, "Ana", "ALUNO", TipoAcao.LOGIN_SUCESSO, null, null, "d", "ip", null);
        when(servico.buscar(any(FiltroLogDTO.class))).thenReturn(new ResultadoBuscaLogDTO(List.of(log), 730));
        when(servico.verificarIntegridade(log)).thenReturn(true);

        controller.listar(null, null, null, null, null, model, session);

        verify(model).addAttribute("totalEncontrado", 730);
        verify(model).addAttribute("cortado", true);
    }

    @Test
    void cadaLinhaVaiParaATelaComOResultadoDaVerificacaoDeIntegridade() {
        LogAuditoria integro = new LogAuditoria(1L, "Ana", "ALUNO", TipoAcao.LOGIN_SUCESSO, null, null, "d", "ip", null);
        LogAuditoria adulterado = new LogAuditoria(2L, "Bia", "ALUNO", TipoAcao.LOGIN_FALHA, null, null, "d", "ip", null);
        when(servico.buscar(any(FiltroLogDTO.class))).thenReturn(new ResultadoBuscaLogDTO(List.of(integro, adulterado), 2));
        lenient().when(servico.verificarIntegridade(integro)).thenReturn(true);
        lenient().when(servico.verificarIntegridade(adulterado)).thenReturn(false);

        controller.listar(null, null, null, null, null, model, session);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LogAuditoriaViewDTO>> visao = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("logs"), visao.capture());
        assertEquals(2, visao.getValue().size());
        assertTrue(visao.getValue().get(0).isIntegro());
        assertFalse(visao.getValue().get(1).isIntegro());
        assertEquals("Ana", visao.getValue().get(0).getUsuarioNome());
    }

    @Test
    void atividadeDeUmUsuarioMostraResumoEAsDuasListas() {
        LogAuditoria feita = new LogAuditoria(7L, "Ana", "ALUNO", TipoAcao.LOGIN_SUCESSO, null, null, "d", "ip", null);
        LogAuditoria sofrida = new LogAuditoria(9L, "Coord", "COORDENADOR", TipoAcao.ATUALIZACAO, "Aluno", 7L, "d", "ip", null);
        AtividadeUsuarioDTO atividade = new AtividadeUsuarioDTO("ALUNO", 7L, "Ana", 1, null, null, 1, 0, List.of("ip"),
                List.of(), List.of(feita), 1, List.of(sofrida));
        when(servico.atividadeDoUsuario("ALUNO", 7L, null)).thenReturn(atividade);
        lenient().when(servico.verificarIntegridade(any(LogAuditoria.class))).thenReturn(true);

        String tela = controller.usuario("ALUNO", 7L, null, model, session);

        assertEquals("admin/logsUsuario", tela);
        verify(model).addAttribute("atividade", atividade);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LogAuditoriaViewDTO>> feitas = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("feitas"), feitas.capture());
        assertEquals("Ana", feitas.getValue().get(0).getUsuarioNome());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LogAuditoriaViewDTO>> sofridas = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("sofridas"), sofridas.capture());
        assertEquals("Coord", sofridas.getValue().get(0).getUsuarioNome());
        verify(model).addAttribute("maximoLinhas", 500);
        verify(model).addAttribute("maximoSofridas", 200);
    }

    @Test
    void oTipoNaUrlPodeVirEmMinusculaEEPadronizado() {
        AtividadeUsuarioDTO vazia = new AtividadeUsuarioDTO("PROFESSOR", 3L, "Carlos", 0, null, null, 0, 0, List.of(), List.of(), List.of(), 0, List.of());
        when(servico.atividadeDoUsuario("PROFESSOR", 3L, null)).thenReturn(vazia);

        assertEquals("admin/logsUsuario", controller.usuario("  professor ", 3L, null, model, session));
    }

    @Test
    void usuarioDesconhecidoPrecisaDoNomeTentado() {
        AtividadeUsuarioDTO tentativas = new AtividadeUsuarioDTO("DESCONHECIDO", null, "x@umc.br", 2, null, null, 0, 2, List.of(),
                List.of(), List.of(), 0, List.of());
        when(servico.atividadeDoUsuario("DESCONHECIDO", null, "x@umc.br")).thenReturn(tentativas);

        assertEquals("admin/logsUsuario", controller.usuario("DESCONHECIDO", null, "x@umc.br", model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("DESCONHECIDO", null, "  ", model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("DESCONHECIDO", null, null, model, session));
    }

    @Test
    void pedidosSemTipoValidoOuSemIdVoltamParaALista() {
        assertEquals("redirect:/admin/logs", controller.usuario(null, 7L, null, model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("", 7L, null, model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("HACKER", 7L, null, model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("ALUNO", null, null, model, session));
        assertEquals("redirect:/admin/logs", controller.usuario("PROFESSOR", null, "Ana", model, session));
        verify(servico, never()).atividadeDoUsuario(anyString(), anyLong(), anyString());
        verify(servico, never()).atividadeDoUsuario(any(), any(), any());
    }
}