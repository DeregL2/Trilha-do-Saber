package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.AlunoFormDTO;
import com.tcc.trilha_do_saber.dto.CadastroAlunoDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Aluno;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogAuditoriaService logAuditoriaService;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder,
                        LogAuditoriaService logAuditoriaService){
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.logAuditoriaService = logAuditoriaService;
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
        aluno = alunoRepository.save(aluno);

        logAuditoriaService.registrar(aluno.getId(), aluno.getNome(), "ALUNO",
                TipoAcao.CADASTRO, "Aluno", aluno.getId(), "Solicitacao de cadastro enviada",
                null, null);

        return aluno;
    }

    public Aluno atualizar(Long id, AlunoFormDTO dto, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setRgm(dto.getRgm());
        aluno.setCurso(dto.getCurso());
        aluno.setSemestre(dto.getSemestre());
        aluno.setAtivo(dto.isAtivo());

        aluno = alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATUALIZACAO, "Aluno", aluno.getId(), "Dados do aluno atualizados", ip, duracaoMs);

        return aluno;
    }

    public void ativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(true);
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATIVACAO, "Aluno", id, null, ip, duracaoMs);
    }

    public void desativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.DESATIVACAO, "Aluno", id, null, ip, duracaoMs);
    }

    public void excluir(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(false);
        aluno.setDataExclusao(LocalDateTime.now());
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.EXCLUSAO, "Aluno", id, null, ip, duracaoMs);
    }

    public void anonimizar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
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

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ANONIMIZACAO, "Aluno", id, null, ip, duracaoMs);
    }
}