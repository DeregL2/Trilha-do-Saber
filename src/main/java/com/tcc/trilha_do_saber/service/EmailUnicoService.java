package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.repository.AdmGeralRepository;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import com.tcc.trilha_do_saber.repository.CoordenadorRepository;
import com.tcc.trilha_do_saber.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailUnicoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final AdmGeralRepository admGeralRepository;

    public EmailUnicoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository,
                             CoordenadorRepository coordenadorRepository, AdmGeralRepository admGeralRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.admGeralRepository = admGeralRepository;
    }

    public boolean emailJaCadastrado(String email) {
        return alunoRepository.findByEmailIgnoreCase(email).isPresent()
                || professorRepository.findByEmailIgnoreCase(email).isPresent()
                || coordenadorRepository.findByEmailIgnoreCase(email).isPresent()
                || admGeralRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public void validarEmailDisponivel(String email) {
        if (emailJaCadastrado(email)) {
            throw new IllegalArgumentException("Já existe um cadastro com esse email");
        }
    }

    public boolean emailJaCadastradoPorOutraConta(String email, String tipoAtual, Long idAtual) {
        boolean naAluno = alunoRepository.findByEmailIgnoreCase(email)
                .map(a -> !("ALUNO".equals(tipoAtual) && a.getId().equals(idAtual)))
                .orElse(false);
        boolean naProfessor = professorRepository.findByEmailIgnoreCase(email)
                .map(p -> !("PROFESSOR".equals(tipoAtual) && p.getId().equals(idAtual)))
                .orElse(false);
        boolean naCoordenador = coordenadorRepository.findByEmailIgnoreCase(email)
                .map(c -> !("COORDENADOR".equals(tipoAtual) && c.getId().equals(idAtual)))
                .orElse(false);
        boolean noAdmin = admGeralRepository.findByEmailIgnoreCase(email)
                .map(ad -> !("ADMIN".equals(tipoAtual) && ad.getId().equals(idAtual)))
                .orElse(false);

        return naAluno || naProfessor || naCoordenador || noAdmin;
    }

    public void validarEmailDisponivelParaEdicao(String email, String tipoAtual, Long idAtual) {
        if (emailJaCadastradoPorOutraConta(email, tipoAtual, idAtual)) {
            throw new IllegalArgumentException("Já existe um cadastro com esse email");
        }
    }
}