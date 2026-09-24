package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.model.AdmGeral;
import com.tcc.trilha_do_saber.model.Coordenador;
import com.tcc.trilha_do_saber.repository.AdmGeralRepository;
import com.tcc.trilha_do_saber.repository.CoordenadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Classe temporaria: garante um admin e um coordenador para conseguir testar o login.
@Profile("!import")
@Component
public class CargaUsuariosIniciais implements CommandLineRunner {

    private final AdmGeralRepository admGeralRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaUsuariosIniciais(AdmGeralRepository admGeralRepository, CoordenadorRepository coordenadorRepository,
                                  PasswordEncoder passwordEncoder){
        this.admGeralRepository = admGeralRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){
        if (admGeralRepository.findByEmail("adm@umc.br").isEmpty()) {
            AdmGeral admin = new AdmGeral("Admin Sistema", "adm@umc.br", passwordEncoder.encode("Admin@123"));
            admin.setAtivo(true);
            admGeralRepository.save(admin);
            System.out.println("Admin de teste criado: adm@umc.br / Admin@123");
        }

        if (coordenadorRepository.findByEmail("coordenacao@umc.br").isEmpty()) {
            Coordenador coordenador = new Coordenador("Coordenação", "coordenacao@umc.br",
                    passwordEncoder.encode("Coord@123"), "20240001", "Sistemas de Informação");
            coordenador.setAtivo(true);
            coordenadorRepository.save(coordenador);
            System.out.println("Coordenador de teste criado: coordenacao@umc.br / Coord@123");
        }
    }
}
