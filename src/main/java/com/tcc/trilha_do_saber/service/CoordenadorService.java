package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.CadastroCoordenadorDTO;
import com.tcc.trilha_do_saber.dto.CoordenadorFormDTO;
import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.model.Coordenador;
import com.tcc.trilha_do_saber.model.TipoAcao;
import com.tcc.trilha_do_saber.repository.CoordenadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogAuditoriaService logAuditoriaService;

    public CoordenadorService(CoordenadorRepository coordenadorRepository, PasswordEncoder passwordEncoder,
                              LogAuditoriaService logAuditoriaService){
        this.coordenadorRepository = coordenadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.logAuditoriaService = logAuditoriaService;
    }

    public List<Coordenador> listarTodos(){
        return coordenadorRepository.findAll();
    }

    public Coordenador buscarPorId(Long id){
        return coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
    }

    public Coordenador cadastrar(CadastroCoordenadorDTO dto){
        if (!dto.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }
        if (coordenadorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cadastro com esse email");
        }

        String senhaComHash = passwordEncoder.encode(dto.getSenha());
        Coordenador coordenador = new Coordenador(dto.getNome(), dto.getEmail(), senhaComHash, dto.getRegistroFuncional(), dto.getCurso());
        coordenador.setConsentimentoLgpd(dto.isAceitouTermos());
        coordenador.setDataConsentimento(LocalDateTime.now());
        coordenador = coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(coordenador.getId(), coordenador.getNome(), "COORDENADOR",
                TipoAcao.CADASTRO, "Coordenador", coordenador.getId(), "Solicitacao de cadastro enviada",
                null, null);

        return coordenador;
    }

    public Coordenador atualizar(Long id, CoordenadorFormDTO dto, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Coordenador coordenador = buscarPorId(id);

        coordenador.setNome(dto.getNome());
        coordenador.setEmail(dto.getEmail());
        coordenador.setRegistroFuncional(dto.getRegistroFuncional());
        coordenador.setCurso(dto.getCurso());
        coordenador.setAtivo(dto.isAtivo());

        coordenador = coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATUALIZACAO, "Coordenador", coordenador.getId(), "Dados do coordenador atualizados",
                ip, duracaoMs);

        return coordenador;
    }

    public void ativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(true);
        coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ATIVACAO, "Coordenador", id, "Cadastro aprovado", ip, duracaoMs);
    }

    public void desativar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(false);
        coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.DESATIVACAO, "Coordenador", id, null, ip, duracaoMs);
    }

    public void excluir(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(false);
        coordenador.setDataExclusao(LocalDateTime.now());
        coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.EXCLUSAO, "Coordenador", id, null, ip, duracaoMs);
    }

    public void anonimizar(Long id, UsuarioSessaoDTO ator, String ip, long duracaoMs){
        Coordenador coordenador = buscarPorId(id);

        coordenador.setNome("Usuário removido");
        coordenador.setEmail("removido-" + coordenador.getId() + "@anonimo.local");
        coordenador.setSenha(null);
        coordenador.setRegistroFuncional(null);
        coordenador.setCurso(null);
        coordenador.setAtivo(false);
        coordenador.setAnonimizado(true);
        if (coordenador.getDataExclusao() == null) {
            coordenador.setDataExclusao(LocalDateTime.now());
        }

        coordenadorRepository.save(coordenador);

        logAuditoriaService.registrar(ator.getId(), ator.getNome(), ator.getTipo(),
                TipoAcao.ANONIMIZACAO, "Coordenador", id, null, ip, duracaoMs);
    }
}