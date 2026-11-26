package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.exception.AcessoNegadoException;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.service.AlunoService;
import com.tcc.trilha_do_saber.service.ImportacaoAlunosService;
import com.tcc.trilha_do_saber.service.MinhaContaService;
import com.tcc.trilha_do_saber.service.ProfessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Coordenador só gerencia alunos dos cursos que coordena; o admin gerencia todos
@ExtendWith(MockitoExtension.class)
class CoordenadorUsuarioEscopoTest {

    @Mock private AlunoService alunoService;
    @Mock private ProfessorService professorService;
    @Mock private MinhaContaService minhaContaService;
    @Mock private ImportacaoAlunosService importacaoAlunosService;

    private CoordenadorUsuarioController controller;
    private MockHttpSession sessao;
    private final UsuarioSessaoDTO coordenador = new UsuarioSessaoDTO(7L, "Coord", "coord@umc.br", "COORDENADOR");
    private final UsuarioSessaoDTO admin = new UsuarioSessaoDTO(1L, "Admin", "adm@umc.br", "ADMIN");

    private final Aluno alunoDeSi = new Aluno("Ana", "ana@umc.br", "h", "RA-1", "Sistemas de Informação", 8);
    private final Aluno alunoDeEs = new Aluno("Bia", "bia@umc.br", "h", "RA-2", "Engenharia de Software", 8);

    @BeforeEach
    void preparar() {
        controller = new CoordenadorUsuarioController(alunoService, professorService, minhaContaService, importacaoAlunosService);
        sessao = new MockHttpSession();
        sessao.setAttribute("usuarioLogado", coordenador);
        lenient().when(importacaoAlunosService.cursosPermitidos(coordenador)).thenReturn(List.of("Sistemas de Informação"));
        lenient().when(alunoService.buscarPorId(1L)).thenReturn(alunoDeSi);
        lenient().when(alunoService.buscarPorId(2L)).thenReturn(alunoDeEs);
    }

    @Test
    void naoExcluiAlunoDeOutroCurso() {
        assertThrows(AcessoNegadoException.class,
                () -> controller.excluir("aluno", 2L, sessao, new MockHttpServletRequest()));
        verify(alunoService, never()).excluir(anyLong(), any(), any(), anyLong());
    }

    @Test
    void excluiAlunoDoProprioCurso() {
        controller.excluir("aluno", 1L, sessao, new MockHttpServletRequest());
        verify(alunoService).excluir(eq(1L), eq(coordenador), any(), anyLong());
    }

    @Test
    void naoAbreEdicaoNemAnonimizaAlunoDeOutroCurso() {
        assertThrows(AcessoNegadoException.class,
                () -> controller.editarFormulario("aluno", 2L, new ExtendedModelMap(), sessao));
        assertThrows(AcessoNegadoException.class,
                () -> controller.anonimizar("aluno", 2L, sessao, new MockHttpServletRequest()));
    }

    @Test
    void listaSoAlunosDosCursosCoordenados() {
        when(alunoService.listarTodos()).thenReturn(List.of(alunoDeSi, alunoDeEs));
        when(professorService.listarTodos()).thenReturn(List.of());
        when(minhaContaService.listarSolicitacoesDeAlunosEProfessores()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        controller.listar(null, null, 1, 1, model, sessao);

        assertEquals(List.of(alunoDeSi), model.get("alunos"));
    }

    @Test
    void adminContinuaVendoTodosOsAlunos() {
        sessao.setAttribute("usuarioLogado", admin);
        when(alunoService.listarTodos()).thenReturn(List.of(alunoDeSi, alunoDeEs));
        when(professorService.listarTodos()).thenReturn(List.of());
        when(minhaContaService.listarSolicitacoesDeAlunosEProfessores()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        controller.listar(null, null, 1, 1, model, sessao);

        assertEquals(List.of(alunoDeSi, alunoDeEs), model.get("alunos"));
    }

    @Test
    void coordenadorNaoAnalisaPedidoDeOutroCoordenador() {
        assertThrows(AcessoNegadoException.class,
                () -> controller.aprovarSolicitacao("coordenador", 3L, sessao, new MockHttpServletRequest()));
        verify(minhaContaService, never()).aprovarSolicitacao(anyString(), anyLong(), any(), any(), anyLong());
    }
}
