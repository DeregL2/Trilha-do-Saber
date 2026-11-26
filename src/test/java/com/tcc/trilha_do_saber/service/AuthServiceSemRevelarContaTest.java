package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.repository.AdmGeralRepository;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import com.tcc.trilha_do_saber.repository.CoordenadorRepository;
import com.tcc.trilha_do_saber.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

// O login não pode dizer se um e-mail existe ou qual a situação da conta para quem não sabe a senha
@ExtendWith(MockitoExtension.class)
class AuthServiceSemRevelarContaTest {

    @Mock private AdmGeralRepository admGeralRepository;
    @Mock private CoordenadorRepository coordenadorRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private AlunoRepository alunoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private AuthService servico;
    private Aluno aluno;

    @BeforeEach
    void preparar() {
        servico = new AuthService(admGeralRepository, coordenadorRepository, professorRepository, alunoRepository, passwordEncoder);
        aluno = new Aluno("Ana", "ana@umc.br", "hash", "RA-1", "Sistemas de Informação", 8, "B", "Noturno");
        lenient().when(admGeralRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(coordenadorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(professorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(alunoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(alunoRepository.findByEmail("ana@umc.br")).thenReturn(Optional.of(aluno));
        lenient().when(passwordEncoder.matches("certa", "hash")).thenReturn(true);
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("hash-ficticio");
    }

    @Test
    void cadastroPendenteComSenhaErradaMostraMensagemGenerica() {
        aluno.setAtivo(false);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> servico.autenticar("ana@umc.br", "errada"));

        assertEquals("E-mail ou senha inválidos", erro.getMessage());
    }

    @Test
    void cadastroPendenteComSenhaCertaInformaQueAguardaAprovacao() {
        aluno.setAtivo(false);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> servico.autenticar("ana@umc.br", "certa"));

        assertEquals("Cadastro ainda não aprovado", erro.getMessage());
    }

    @Test
    void contaExcluidaComSenhaErradaMostraMensagemGenerica() {
        aluno.setAtivo(true);
        aluno.setDataExclusao(LocalDateTime.now());

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> servico.autenticar("ana@umc.br", "errada"));

        assertEquals("E-mail ou senha inválidos", erro.getMessage());
    }

    @Test
    void emailInexistenteTambemConfereUmaSenhaParaNaoDenunciarPeloTempo() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> servico.autenticar("ninguem@umc.br", "qualquer"));

        assertEquals("E-mail ou senha inválidos", erro.getMessage());
        verify(passwordEncoder).matches(any(), any());
    }
}
