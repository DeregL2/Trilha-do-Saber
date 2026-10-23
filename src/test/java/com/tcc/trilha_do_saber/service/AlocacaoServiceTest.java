package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.AlocacaoFormDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.AlocacaoDisciplina;
import com.tcc.trilha_do_saber.model.Professor;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.repository.AlocacaoDisciplinaRepository;
import com.tcc.trilha_do_saber.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlocacaoServiceTest {

    private static final String SI = "Sistemas de Informação";
    private static final String ES = "Engenharia de Software";

    private final UsuarioSessaoDTO coordenador = new UsuarioSessaoDTO(9L, "Coordenação", "coord@umc.br", "COORDENADOR");

    @Mock
    private AlocacaoDisciplinaRepository alocacaoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private LogAuditoriaService logAuditoriaService;

    @InjectMocks
    private AlocacaoService alocacaoService;

    @Test
    void atualizarDeveAlterarOsCamposERegistrarCadaMudancaNoLog() {
        Professor ana = professor(1L, "Ana", true, false);
        Professor bia = professor(2L, "Bia", true, false);
        AlocacaoDisciplina alocacao = alocacao(5L, SI, ana);
        when(professorRepository.findById(2L)).thenReturn(Optional.of(bia));
        when(alocacaoRepository.findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurnoAndDisciplinaIgnoreCase(
                "2026/2", SI, 7, "C", "Integral", "Redes")).thenReturn(Optional.empty());
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));
        when(alocacaoRepository.save(any(AlocacaoDisciplina.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        alocacaoService.atualizar(5L, formulario(7, "C", "Integral", "  Redes  ", 2L), List.of(SI), coordenador, "127.0.0.1");

        verify(alocacao).setSemestre(7);
        verify(alocacao).setTurma("C");
        verify(alocacao).setTurno("Integral");
        verify(alocacao).setDisciplina("Redes");
        verify(alocacao).setProfessor(bia);
        ArgumentCaptor<String> detalhes = ArgumentCaptor.forClass(String.class);
        verify(logAuditoriaService).registrar(eq(9L), eq("Coordenação"), eq("COORDENADOR"),
                eq(TipoAcao.ATUALIZACAO), eq("Alocacao"), eq(5L), detalhes.capture(), eq("127.0.0.1"), any());
        assertTrue(detalhes.getValue().contains("semestre de '8' para '7'"));
        assertTrue(detalhes.getValue().contains("turma de 'B' para 'C'"));
        assertTrue(detalhes.getValue().contains("turno de 'Noturno' para 'Integral'"));
        assertTrue(detalhes.getValue().contains("disciplina de 'Banco de Dados' para 'Redes'"));
        assertTrue(detalhes.getValue().contains("professor de 'Ana' para 'Bia'"));
    }

    @Test
    void salvarSemMudancasDeveRegistrarQueNaoHouveAlteracao() {
        Professor ana = professor(1L, "Ana", true, false);
        AlocacaoDisciplina alocacao = alocacao(5L, SI, ana);
        when(professorRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));
        when(alocacaoRepository.findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurnoAndDisciplinaIgnoreCase(
                "2026/2", SI, 8, "B", "Noturno", "Banco de Dados")).thenReturn(Optional.of(alocacao));
        when(alocacaoRepository.save(any(AlocacaoDisciplina.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Banco de Dados", 1L), List.of(SI), coordenador, "127.0.0.1");

        ArgumentCaptor<String> detalhes = ArgumentCaptor.forClass(String.class);
        verify(logAuditoriaService).registrar(any(), any(), any(), any(), any(), any(), detalhes.capture(), any(), any());
        assertTrue(detalhes.getValue().contains("sem alteracoes"));
    }

    @Test
    void coordenadorNaoPodeEditarTurmaDeOutroCurso() {
        AlocacaoDisciplina alocacao = alocacao(5L, ES, professor(1L, "Ana", true, false));
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Redes", 1L), List.of(SI), coordenador, "127.0.0.1"));

        assertTrue(erro.getMessage().contains("Você não pode editar turmas do curso Engenharia de Software"));
        verify(alocacaoRepository, never()).save(any());
    }

    @Test
    void turmaInexistenteDeveSerRecusada() {
        when(alocacaoRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.buscarPermitida(99L, List.of(SI)));

        assertEquals("Turma não encontrada.", erro.getMessage());
    }

    @Test
    void professorInexistenteOuSemCadastroAtivoDeveSerRecusado() {
        AlocacaoDisciplina alocacao = alocacao(5L, SI, professor(1L, "Ana", true, false));
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));
        Professor pendente = professor(3L, "Caio", false, false);
        Professor excluido = professor(4L, "Davi", false, true);
        doReturn(Optional.empty()).when(professorRepository).findById(7L);
        doReturn(Optional.of(pendente)).when(professorRepository).findById(3L);
        doReturn(Optional.of(excluido)).when(professorRepository).findById(4L);

        assertEquals("Professor não encontrado.", assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Redes", 7L), List.of(SI), coordenador, "ip")).getMessage());
        assertEquals("Selecione um professor com cadastro ativo.", assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Redes", 3L), List.of(SI), coordenador, "ip")).getMessage());
        assertEquals("Selecione um professor com cadastro ativo.", assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Redes", 4L), List.of(SI), coordenador, "ip")).getMessage());
        verify(alocacaoRepository, never()).save(any());
    }

    @Test
    void disciplinaQueJaExisteEmOutroRegistroDaMesmaTurmaDeveSerRecusada() {
        Professor ana = professor(1L, "Ana", true, false);
        AlocacaoDisciplina alocacao = alocacao(5L, SI, ana);
        AlocacaoDisciplina outra = alocacao(9L, SI, ana);
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(alocacaoRepository.findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurnoAndDisciplinaIgnoreCase(
                "2026/2", SI, 8, "B", "Noturno", "Redes")).thenReturn(Optional.of(outra));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () ->
                alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "Redes", 1L), List.of(SI), coordenador, "ip"));

        assertTrue(erro.getMessage().contains("Já existe a disciplina Redes"));
        verify(alocacaoRepository, never()).save(any());
    }

    @Test
    void trocarSoMaiusculasDaPropriaDisciplinaDeveSerPermitido() {
        Professor ana = professor(1L, "Ana", true, false);
        AlocacaoDisciplina alocacao = alocacao(5L, SI, ana);
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(alocacaoRepository.findByPeriodoLetivoAndCursoAndSemestreAndTurmaAndTurnoAndDisciplinaIgnoreCase(
                "2026/2", SI, 8, "B", "Noturno", "BANCO DE DADOS")).thenReturn(Optional.of(alocacao));
        when(alocacaoRepository.save(any(AlocacaoDisciplina.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        alocacaoService.atualizar(5L, formulario(8, "B", "Noturno", "BANCO DE DADOS", 1L), List.of(SI), coordenador, "ip");

        verify(alocacao).setDisciplina("BANCO DE DADOS");
    }

    @Test
    void professoresDisponiveisDevemSerSoOsAtivosEmOrdemAlfabetica() {
        Professor caio = professor(1L, "Caio", true, false);
        Professor ana = professor(2L, "ana", true, false);
        Professor davi = professor(3L, "Davi", false, false);
        Professor eva = professor(4L, "Eva", false, true);
        when(professorRepository.findAll()).thenReturn(List.of(caio, ana, davi, eva));

        List<Professor> disponiveis = alocacaoService.professoresDisponiveis();

        assertEquals(2, disponiveis.size());
        assertEquals("ana", disponiveis.get(0).getNome());
        assertEquals("Caio", disponiveis.get(1).getNome());
    }

    @Test
    void buscarPermitidaDeveIgnorarMaiusculasNoNomeDoCurso() {
        AlocacaoDisciplina alocacao = alocacao(5L, SI, professor(1L, "Ana", true, false));
        when(alocacaoRepository.findById(5L)).thenReturn(Optional.of(alocacao));

        assertSame(alocacao, alocacaoService.buscarPermitida(5L, List.of("sistemas de informação")));
    }

    private AlocacaoFormDTO formulario(int semestre, String turma, String turno, String disciplina, Long professorId) {
        AlocacaoFormDTO form = new AlocacaoFormDTO();
        form.setSemestre(semestre);
        form.setTurma(turma);
        form.setTurno(turno);
        form.setDisciplina(disciplina);
        form.setProfessorId(professorId);
        return form;
    }

    private AlocacaoDisciplina alocacao(long id, String curso, Professor professor) {
        AlocacaoDisciplina alocacao = mock(AlocacaoDisciplina.class);
        lenient().when(alocacao.getId()).thenReturn(id);
        lenient().when(alocacao.getPeriodoLetivo()).thenReturn("2026/2");
        lenient().when(alocacao.getCurso()).thenReturn(curso);
        lenient().when(alocacao.getSemestre()).thenReturn(8);
        lenient().when(alocacao.getTurma()).thenReturn("B");
        lenient().when(alocacao.getTurno()).thenReturn("Noturno");
        lenient().when(alocacao.getDisciplina()).thenReturn("Banco de Dados");
        lenient().when(alocacao.getProfessor()).thenReturn(professor);
        return alocacao;
    }

    private Professor professor(long id, String nome, boolean ativo, boolean excluido) {
        Professor professor = mock(Professor.class);
        lenient().when(professor.getId()).thenReturn(id);
        lenient().when(professor.getNome()).thenReturn(nome);
        lenient().when(professor.isAtivo()).thenReturn(ativo);
        lenient().when(professor.isExcluido()).thenReturn(excluido);
        return professor;
    }
}