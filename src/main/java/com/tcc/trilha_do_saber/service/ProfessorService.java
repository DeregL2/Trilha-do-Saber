package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.CadastroProfessorDTO;
import com.tcc.trilha_do_saber.dto.ProfessorFormDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Professor;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.repository.ProfessorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogAuditoriaService logAuditoriaService;

    public ProfessorService(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder,
                            LogAuditoriaService logAuditoriaService){
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.logAuditoriaService = logAuditoriaService;
    }

    public List<Professor> listarTodos(){
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public Professor cadastrar(CadastroProfessorDTO dto){
        if (!dto.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }
        if (professorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cadastro com esse email");
        }

        String senhaComHash = passwordEncoder.encode(dto.getSenha());
        Professor professor = new Professor(dto.getNome(), dto.getEmail(), senhaComHash, dto.getRegistroProfissional());
        professor.setConsentimentoLgpd(dto.isAceitouTermos());
        professor.setDataConsentimento(LocalDateTime.now());
        professor = professorRepository.save(professor);

        logAuditoriaService.registrar(professor.getId(), professor.getNome(), "PROFESSOR",
                TipoAcao.CADASTRO, "Professor", professor.getId(), "Solicitacao de cadastro enviada",
                null, null);

        return professor;
    }

    public Professor atualizar(Long id, ProfessorFormDTO dto, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Professor professor = buscarPorId(id);

        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setRegistroProfissional(dto.getRegistroProfissional());
        professor.setDisciplina(dto.getDisciplina());
        professor.setAtivo(dto.isAtivo());

        professor = professorRepository.save(professor);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATUALIZACAO, "Professor", professor.getId(), "Dados do professor atualizados", ip, duracaoMs);

        return professor;
    }

    public void ativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Professor professor = buscarPorId(id);
        professor.setAtivo(true);
        professorRepository.save(professor);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATIVACAO, "Professor", id, null, ip, duracaoMs);
    }

    public void desativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Professor professor = buscarPorId(id);
        professor.setAtivo(false);
        professorRepository.save(professor);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.DESATIVACAO, "Professor", id, null, ip, duracaoMs);
    }

    public void excluir(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Professor professor = buscarPorId(id);
        professor.setAtivo(false);
        professor.setDataExclusao(LocalDateTime.now());
        professorRepository.save(professor);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.EXCLUSAO, "Professor", id, null, ip, duracaoMs);
    }

    public void anonimizar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Professor professor = buscarPorId(id);

        professor.setNome("Usuário removido");
        professor.setEmail("removido-" + professor.getId() + "@anonimo.local");
        professor.setSenha(null);
        professor.setRegistroProfissional(null);
        professor.setDisciplina(null);
        professor.setAtivo(false);
        professor.setAnonimizado(true);
        if (professor.getDataExclusao() == null) {
            professor.setDataExclusao(LocalDateTime.now());
        }

        professorRepository.save(professor);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ANONIMIZACAO, "Professor", id, null, ip, duracaoMs);
    }
}