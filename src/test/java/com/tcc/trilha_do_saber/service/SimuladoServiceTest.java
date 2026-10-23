package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.exception.SemQuestoesException;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Simulado;
import com.tcc.trilha_do_saber.model.SimuladoQuestao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import com.tcc.trilha_do_saber.repository.RespostaRepository;
import com.tcc.trilha_do_saber.repository.SimuladoQuestaoRepository;
import com.tcc.trilha_do_saber.repository.SimuladoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimuladoServiceTest {

    @Mock
    private SimuladoRepository simuladoRepository;

    @Mock
    private SimuladoQuestaoRepository simuladoQuestaoRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private AlternativaRepository alternativaRepository;

    @Mock
    private RespostaRepository respostaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private SimuladoService simuladoService;

    @Test
    void iniciarDeveInformarOCursoQuandoNaoHaQuestoesCadastradas() {
        Aluno aluno = mock(Aluno.class);
        when(aluno.getCurso()).thenReturn("Engenharia de Software");
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(questaoRepository.findByProvaCurso("Engenharia de Software")).thenReturn(List.of());

        SemQuestoesException excecao = assertThrows(SemQuestoesException.class, () -> simuladoService.iniciar(1L));

        assertEquals("Engenharia de Software", excecao.getCurso());
        verify(simuladoRepository, never()).save(any());
    }

    @Test
    void iniciarDeveSortearNoMaximoCincoQuestoesDoCursoDoAluno() {
        Aluno aluno = mock(Aluno.class);
        when(aluno.getCurso()).thenReturn("Sistemas de Informação");
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        List<Questao> banco = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            banco.add(mock(Questao.class));
        }
        when(questaoRepository.findByProvaCurso("Sistemas de Informação")).thenReturn(banco);
        when(simuladoRepository.save(any(Simulado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        simuladoService.iniciar(1L);

        verify(simuladoQuestaoRepository, times(5)).save(any(SimuladoQuestao.class));
    }
}