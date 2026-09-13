package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.ProfessorFormDTO;
import com.tcc.trilha_do_saber.model.Professor;
import com.tcc.trilha_do_saber.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository){
        this.professorRepository = professorRepository;
    }

    public List<Professor> listarTodos(){
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public Professor criar(ProfessorFormDTO dto){
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para criar o usuário");
        }
        if (professorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um professor com esse email");
        }

        Professor professor = new Professor(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getRegistroProfissional(), dto.getDisciplina());
        return professorRepository.save(professor);
    }

    public Professor atualizar(Long id, ProfessorFormDTO dto){
        Professor professor = buscarPorId(id);

        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setRegistroProfissional(dto.getRegistroProfissional());
        professor.setDisciplina(dto.getDisciplina());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            professor.setSenha(dto.getSenha());
        }

        return professorRepository.save(professor);
    }

    public void excluir(Long id){
        professorRepository.deleteById(id);
    }
}
