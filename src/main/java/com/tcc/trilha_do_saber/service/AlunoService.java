package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.AlunoFormDTO;
import com.tcc.trilha_do_saber.dto.CadastroAlunoDTO;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder){
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Aluno> listarTodos(){
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public Aluno cadastrar(CadastroAlunoDTO dto){
        if (!dto.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }
        if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cadastro com esse email");
        }

        String senhaComHash = passwordEncoder.encode(dto.getSenha());
        Aluno aluno = new Aluno(dto.getNome(), dto.getEmail(), senhaComHash, dto.getMatricula(), dto.getCurso(), dto.getSemestre());
        aluno.setConsentimentoLgpd(dto.isAceitouTermos());
        aluno.setDataConsentimento(LocalDateTime.now());
        return alunoRepository.save(aluno);
    }
    public Aluno atualizar(Long id, AlunoFormDTO dto){
        Aluno aluno = buscarPorId(id);

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setRgm(dto.getRgm());
        aluno.setCurso(dto.getCurso());
        aluno.setSemestre(dto.getSemestre());
        aluno.setAtivo(dto.isAtivo());

        return alunoRepository.save(aluno);
    }

    public void ativar(Long id){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(true);
        alunoRepository.save(aluno);
    }

    public void desativar(Long id){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    public void excluir(Long id){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(false);
        aluno.setDataExclusao(LocalDateTime.now());
        alunoRepository.save(aluno);
    }

    public void anonimizar(Long id){
        Aluno aluno = buscarPorId(id);

        aluno.setNome("Usuário removido");
        aluno.setEmail("removido-" + aluno.getId() + "@anonimo.local");
        aluno.setSenha(null);
        aluno.setRgm(null);
        aluno.setAtivo(false);
        aluno.setAnonimizado(true);
        if (aluno.getDataExclusao() == null) {
            aluno.setDataExclusao(LocalDateTime.now());
        }

        alunoRepository.save(aluno);
    }
}

