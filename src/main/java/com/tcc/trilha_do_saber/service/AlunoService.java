package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.AlunoFormDTO;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository){
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarTodos(){
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public Aluno criar(AlunoFormDTO dto){
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para criar o usuário");
        }
        if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno com esse email");
        }

        Aluno aluno = new Aluno(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getRa(), dto.getCurso(), dto.getSemestre());
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, AlunoFormDTO dto){
        Aluno aluno = buscarPorId(id);

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setRa(dto.getRa());
        aluno.setCurso(dto.getCurso());
        aluno.setSemestre(dto.getSemestre());

<<<<<<< HEAD
=======
        // Só troca a senha se o coordenador digitou uma nova
>>>>>>> main
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            aluno.setSenha(dto.getSenha());
        }

        return alunoRepository.save(aluno);
    }

    public void excluir(Long id){
        alunoRepository.deleteById(id);
    }
}
