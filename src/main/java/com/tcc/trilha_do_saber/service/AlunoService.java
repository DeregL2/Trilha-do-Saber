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

    // Construtor
    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder,
                        LogAuditoriaService logAuditoriaService){
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.logAuditoriaService = logAuditoriaService;
    }

    // Lista todos os alunos cadastrados
    public List<Aluno> listarTodos(){
        return alunoRepository.findAll();
    }

    // Busca um aluno pelo id, lanca erro se nao encontrar
    public Aluno buscarPorId(Long id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    // Cadastro publico de aluno; fica inativo ate a coordenacao aprovar
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

        // Cadastro ainda nao tem "ator" (ninguem logado fez essa acao, foi o proprio aluno se cadastrando)
        logAuditoriaService.registrar(aluno.getId(), aluno.getNome(), "ALUNO",
                TipoAcao.CADASTRO, "Aluno", aluno.getId(),
                "Solicitacao de cadastro enviada por " + aluno.getNome() + " (" + aluno.getEmail() + "), aguardando aprovacao",
                null, null);

        return aluno;
    }

    // Compara valor antigo x novo de um campo e acumula a diferenca numa string, pro log de auditoria
    private void compararCampo(StringBuilder mudancas, String nomeCampo, String valorAntigo, String valorNovo){
        boolean mudou = valorAntigo == null ? valorNovo != null : !valorAntigo.equals(valorNovo);
        if (mudou) {
            if (mudancas.length() > 0) mudancas.append("; ");
            mudancas.append(nomeCampo).append(" de '").append(valorAntigo).append("' para '").append(valorNovo).append("'");
        }
    }

    // Atualiza os dados do aluno e registra no log exatamente quais campos mudaram
    public Aluno atualizar(Long id, AlunoFormDTO dto, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);

        StringBuilder mudancas = new StringBuilder();
        compararCampo(mudancas, "nome", aluno.getNome(), dto.getNome());
        compararCampo(mudancas, "email", aluno.getEmail(), dto.getEmail());
        compararCampo(mudancas, "RGM", aluno.getRgm(), dto.getRgm());
        compararCampo(mudancas, "curso", aluno.getCurso(), dto.getCurso());
        compararCampo(mudancas, "semestre", String.valueOf(aluno.getSemestre()), String.valueOf(dto.getSemestre()));
        compararCampo(mudancas, "status", aluno.isAtivo() ? "ativo" : "inativo", dto.isAtivo() ? "ativo" : "inativo");

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setRgm(dto.getRgm());
        aluno.setCurso(dto.getCurso());
        aluno.setSemestre(dto.getSemestre());
        aluno.setAtivo(dto.isAtivo());

        aluno = alunoRepository.save(aluno);

        String detalhes = mudancas.length() > 0
                ? "Alteracoes em " + aluno.getNome() + ": " + mudancas
                : "Formulario salvo sem alteracoes em " + aluno.getNome();

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATUALIZACAO, "Aluno", aluno.getId(), detalhes, ip, duracaoMs);

        return aluno;
    }

    // Aprova o cadastro do aluno, liberando o acesso a plataforma
    public void ativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(true);
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATIVACAO, "Aluno", id,
                "Aluno " + aluno.getNome() + " (" + aluno.getEmail() + ") ativado, acesso liberado",
                ip, duracaoMs);
    }

    // Bloqueia o acesso do aluno sem apagar os dados
    public void desativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.DESATIVACAO, "Aluno", id,
                "Aluno " + aluno.getNome() + " (" + aluno.getEmail() + ") desativado, acesso bloqueado",
                ip, duracaoMs);
    }

    // Exclusao "soft": desativa e marca data de exclusao, mas mantem os dados academicos no banco
    public void excluir(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Aluno aluno = buscarPorId(id);

        // Guarda nome/email antes de mexer no registro, pra descrever no log com clareza
        String nomeParaLog = aluno.getNome();
        String emailParaLog = aluno.getEmail();

        aluno.setAtivo(false);
        aluno.setDataExclusao(LocalDateTime.now());
        alunoRepository.save(aluno);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.EXCLUSAO, "Aluno", id,
                "Aluno excluido: " + nomeParaLog + " (" + emailParaLog + "). Dados academicos mantidos no banco.",
                ip, duracaoMs);
    }

    // Anonimizacao definitiva (LGPD): apaga os dados que identificam a pessoa, sem excluir o registro
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

        // Aqui o log nao guarda nome/email antigos de proposito: eles ja foram apagados do registro
        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ANONIMIZACAO, "Aluno", id,
                "Dados pessoais do aluno (id " + id + ") anonimizados definitivamente: nome, email, senha e RGM apagados",
                ip, duracaoMs);
    }
}