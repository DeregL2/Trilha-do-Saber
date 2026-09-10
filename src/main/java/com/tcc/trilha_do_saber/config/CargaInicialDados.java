package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Dificuldade;
import com.tcc.trilha_do_saber.model.Prova;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Tema;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.ProvaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import com.tcc.trilha_do_saber.repository.TemaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Classe temporaria. Gera questoes de teste enquanto o importador de PDF nao fica pronto.
@Component // Classe como bean
public class CargaInicialDados implements CommandLineRunner {

    private final TemaRepository temaRepository;
    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;

    // Construtor da Classe
    public CargaInicialDados(TemaRepository temaRepository,
                             ProvaRepository provaRepository,
                             QuestaoRepository questaoRepository,
                             AlternativaRepository alternativaRepository) {

        this.temaRepository = temaRepository;
        this.provaRepository = provaRepository;
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
    }

    // O Spring executa esse metodo sozinho quando a aplicacao termina de subir
    @Override
    public void run(String... args) {


        boolean cargaJaRodou = temaRepository.findAll().stream()
                .anyMatch(t -> t.getNome().equals("Engenharia de Software"));

        if (cargaJaRodou) {
            System.out.println("Carga inicial ignorada: temas de teste ja existem.");
            return;
        }

        List<Tema> temas = new ArrayList<>();
        temas.add(temaRepository.save(new Tema("Engenharia de Software", "Tema provisorio")));
        temas.add(temaRepository.save(new Tema("Banco de Dados", "Tema provisorio")));
        temas.add(temaRepository.save(new Tema("Redes de Computadores", "Tema provisorio")));

        // Duas edicoes do mesmo curso, para testar o simulado misturando anos diferentes
        List<Prova> provas = new ArrayList<>();
        provas.add(provaRepository.save(new Prova(2021, "Sistemas de Informação")));
        provas.add(provaRepository.save(new Prova(2023, "Sistemas de Informação")));;

        Random sorteio = new Random();

        Dificuldade[] niveis = Dificuldade.values(); // Devolve [FACIL, MEDIO, DIFICIL]

        int totalQuestoes = 30;

        for (int i = 1; i <= totalQuestoes; i++) {

            // O resto da divisao faz o indice girar e distribui as questoes
            Tema tema = temas.get(i % temas.size());
            Dificuldade dificuldade = niveis[i % niveis.length];
            Prova prova = provas.get(i % provas.size());

            // Fonte vai null: nem toda questao do ENADE tem texto de apoio
            Questao questao = questaoRepository.save(
                    new Questao("Enunciado provisorio da questao numero " + i,
                            dificuldade, tema, prova, null));

            // Sorteia qual das cinco alternativas sera a correta
            int posicaoCorreta = sorteio.nextInt(5);

            for (int j = 0; j < 5; j++) {

                char letra = (char) ('A' + j); // 'A' + 1 da 'B', 'A' + 2 da 'C'...

                boolean correta = (j == posicaoCorreta); // So da true na sorteada

                alternativaRepository.save(
                        new Alternativa("Alternativa " + letra + " da questao " + i, correta, questao));
            }
        }

        System.out.println("Carga inicial concluida: " + totalQuestoes + " questoes criadas.");
    }
}