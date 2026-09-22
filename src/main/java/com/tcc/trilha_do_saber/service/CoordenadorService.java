package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.dto.CadastroCoordenadorDTO;
import com.tcc.trilha_do_saber.model.Coordenador;
import com.tcc.trilha_do_saber.repository.CoordenadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final PasswordEncoder passwordEncoder;

    public CoordenadorService(CoordenadorRepository coordenadorRepository, PasswordEncoder passwordEncoder){
        this.coordenadorRepository = coordenadorRepository;
        this.passwordEncoder = passwordEncoder;
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
        return coordenadorRepository.save(coordenador);
    }

    public void ativar(Long id){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(true);
        coordenadorRepository.save(coordenador);
    }

    public void desativar(Long id){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(false);
        coordenadorRepository.save(coordenador);
    }

    public void excluir(Long id){
        Coordenador coordenador = buscarPorId(id);
        coordenador.setAtivo(false);
        coordenador.setDataExclusao(LocalDateTime.now());
        coordenadorRepository.save(coordenador);
    }
}
