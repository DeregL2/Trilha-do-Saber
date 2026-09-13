package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Dificuldade;
import com.tcc.trilha_do_saber.model.Prova;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.ProvaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

// Classe temporaria.
@Profile("!import")
@Component
public class CargaInicialDados implements CommandLineRunner {

    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;

    public CargaInicialDados(ProvaRepository provaRepository,
                             QuestaoRepository questaoRepository,
                             AlternativaRepository alternativaRepository) {

        this.provaRepository = provaRepository;
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
    }

    // Cada linha: enunciado, as 5 alternativas na ordem A-E, e o indice (0-4) da correta
    private static final Object[][] QUESTOES = {

            {
                    "A chance de uma crianca de baixa renda ter um futuro melhor que a realidade em que nasceu esta, " +
                            "em maior ou menor grau, relacionada a escolaridade e ao nivel de renda de seus pais. Nos paises " +
                            "ricos, o \"elevador social\" anda mais rapido. Nos emergentes, mais devagar. No Brasil, ainda mais " +
                            "lentamente. O pais ocupa a segunda pior posicao em um estudo sobre mobilidade social feito pela " +
                            "OCDE, em 2018, com dados de 30 paises. A partir das informacoes apresentadas, e correto afirmar que",
                    new String[]{
                            "o fator ambiental e o fator demografico afetam a mobilidade social observada, sendo ela menor " +
                                    "nos paises que apresentam as maiores taxas de natalidade.",
                            "a baixa organizacao social dos economicamente menos favorecidos determina a baixa mobilidade " +
                                    "social da base para o topo da piramide.",
                            "a mobilidade social e caracterizada por um fator ancestral que se revela ao longo das geracoes, " +
                                    "sendo um limitador da eficacia de politicas publicas de reducao das desigualdades sociais.",
                            "a analise de mobilidade social permite a observacao de um ciclo vicioso, que se caracteriza por " +
                                    "uma subida nas camadas sociais seguida de uma queda, repetindo-se esse ciclo de modo sucessivo.",
                            "a ascensao social depende de fatores viabilizadores que estao fora do alcance das camadas " +
                                    "pobres, o que ocasiona conflitos sociais em busca do acesso a tais fatores."
                    },
                    4 // E
            },

            {
                    "Considerando um texto sobre criterios de aparencia e qualidade de hortalicas e outro sobre o " +
                            "desperdicio de alimentos na cadeia produtiva, avalie as assercoes a seguir e a relacao proposta " +
                            "entre elas.\n\nI. Ha criterios distintivos entre alteracoes visuais que tem efeitos puramente " +
                            "esteticos em produtos alimenticios e aquelas que tem implicacoes na qualidade desses produtos.\n\n" +
                            "PORQUE\n\nII. O aumento das perdas na cadeia produtiva de hortalicas no Brasil e proporcional a " +
                            "elevacao das exigencias dos consumidores pela aparencia de produtos agropecuarios.\n\n" +
                            "A respeito dessas assercoes, assinale a opcao correta.",
                    new String[]{
                            "As assercoes I e II sao proposicoes verdadeiras, e a II e uma justificativa correta da I.",
                            "As assercoes I e II sao proposicoes verdadeiras, mas a II nao e uma justificativa correta da I.",
                            "A assercao I e uma proposicao verdadeira, e a II e uma proposicao falsa.",
                            "A assercao I e uma proposicao falsa, e a II e uma proposicao verdadeira.",
                            "As assercoes I e II sao proposicoes falsas."
                    },
                    2 // C
            },

            {
                    "Considerando o uso de bicicletas como alternativa para melhorar a qualidade de vida nas cidades, " +
                            "avalie as afirmacoes a seguir.\n\nI. Dado que as bicicletas ocupam pouco espaco na malha viaria, " +
                            "prescinde-se de investimentos publicos em ciclovias, sendo prioritarias campanhas de " +
                            "conscientizacao de motoristas.\n\nII. O uso das bicicletas como meio de transporte contribui para " +
                            "a melhoria da qualidade de vida nas grandes metropoles, pois elas nao emitem poluentes, alem de " +
                            "proporcionar a pratica de atividade fisica.\n\nIII. A partir da Segunda Guerra Mundial, durante o " +
                            "governo da Alemanha nazista, o uso da bicicleta tornou-se eficaz e passou a prevalecer nas cidades " +
                            "europeias.\n\nE correto o que se afirma em",
                    new String[]{
                            "I, apenas.",
                            "II, apenas.",
                            "I e III, apenas.",
                            "II e III, apenas.",
                            "I, II e III."
                    },
                    1 // B
            },

            {
                    "Sobre a chamada Gig Economy, ou economia dos bicos, em que trabalhadores prestam servicos por " +
                            "meio de plataformas digitais sem vinculo empregaticio, avalie as assercoes a seguir e a relacao " +
                            "proposta entre elas.\n\nI. Trabalhadores autonomos informais que atuam em plataformas digitais " +
                            "sem qualquer vinculo empregaticio, desprotegidos de regulamentacao ou lei trabalhista, compoem a " +
                            "Gig Economy.\n\nPORQUE\n\nII. Os trabalhadores, na Gig Economy, arcam com todos os custos " +
                            "necessarios para desempenhar o seu trabalho, ganham por producao e enfrentam longas jornadas " +
                            "diarias, o que os deixa mais desgastados e com problemas de saude.\n\n" +
                            "A respeito dessas assercoes, assinale a opcao correta.",
                    new String[]{
                            "As assercoes I e II sao proposicoes verdadeiras, e a II e uma justificativa correta da I.",
                            "As assercoes I e II sao proposicoes verdadeiras, mas a II nao e uma justificativa correta da I.",
                            "A assercao I e uma proposicao verdadeira, e a II e uma proposicao falsa.",
                            "A assercao I e uma proposicao falsa, e a II e uma proposicao verdadeira.",
                            "As assercoes I e II sao proposicoes falsas."
                    },
                    1 // B
            },

            {
                    "Considerando o alto indice de suicidio entre criancas e adolescentes indigenas no Brasil, avalie " +
                            "as afirmacoes a seguir.\n\nI. O elevado indice de suicidios entre criancas e adolescentes " +
                            "indigenas no pais evidencia a necessidade de acoes com foco nos direitos fundamentais desses " +
                            "individuos.\n\nII. Os estados do Para e de Tocantins sao os que possuem os maiores indices de " +
                            "suicidio de indigenas na faixa etaria de 10 a 14 anos.\n\nIII. Os povos das tribos originarias do " +
                            "Brasil, no que tange a sua historia e preservacao cultural, nao estao amparados por direitos e " +
                            "garantias constitucionais.\n\nIV. O estabelecimento de acoes preventivas ao suicidio nas " +
                            "comunidades indigenas deve considerar os elementos globais que afetam a populacao em geral, na " +
                            "faixa etaria entre 15 e 20 anos.\n\nE correto apenas o que se afirma em",
                    new String[]{
                            "I.",
                            "II.",
                            "I e III.",
                            "II e IV.",
                            "III e IV."
                    },
                    0 // A
            },

            {
                    "Considerando o impacto da pandemia de Covid-19 na expectativa de vida de diferentes grupos " +
                            "populacionais nos Estados Unidos, avalie as assercoes a seguir e a relacao proposta entre elas." +
                            "\n\nI. O efeito desproporcional da pandemia da Covid-19 na expectativa de vida da populacao negra " +
                            "e latino-americana estabelece relacao com sua situacao de vulnerabilidade social.\n\nPORQUE\n\n" +
                            "II. Uma hipotese que pode ser levantada quanto a diminuicao da expectativa de vida de negros e " +
                            "latino-americanos esta relacionada as suas precarias condicoes de trabalho, levando-os a maior " +
                            "possibilidade de exposicao ao contagio pelo novo Coronavirus.\n\n" +
                            "A respeito dessas assercoes, assinale a opcao correta.",
                    new String[]{
                            "As assercoes I e II sao proposicoes verdadeiras, e a II e uma justificativa correta da I.",
                            "As assercoes I e II sao proposicoes verdadeiras, mas a II nao e uma justificativa correta da I.",
                            "A assercao I e uma proposicao verdadeira, e a II e uma proposicao falsa.",
                            "A assercao I e uma proposicao falsa, e a II e uma proposicao verdadeira.",
                            "As assercoes I e II sao proposicoes falsas."
                    },
                    0 // A
            },

            {
                    "Considerando a busca por informacoes de saude na internet e seus riscos e beneficios, avalie as " +
                            "afirmacoes a seguir.\n\nI. As buscas realizadas por usuarios da internet sobre patologias exigem " +
                            "criterios, pois algumas informacoes podem trazer riscos a saude por fomentarem a compreensao " +
                            "equivocada de sintomas e profilaxias.\n\nII. A disponibilizacao de informacoes sobre temas de " +
                            "saude nos meios de comunicacao tem contribuido para o esclarecimento da populacao acerca de " +
                            "habitos saudaveis.\n\nIII. Defende-se o acesso a informacoes relativas a pesquisas da area da " +
                            "saude nos veiculos de comunicacao, pois elas permitem que o individuo seja proativo na prevencao " +
                            "de patologias.\n\nE correto o que se afirma em",
                    new String[]{
                            "I, apenas.",
                            "III, apenas.",
                            "I e II, apenas.",
                            "II e III, apenas.",
                            "I, II e III."
                    },
                    2 // C
            },

            {
                    "Considerando os modelos de democracia majoritaria e democracia consensual propostos pelo " +
                            "politologo Arend Lijphart, avalie as afirmacoes a seguir.\n\nI. O bem comum, a ser estabelecido " +
                            "por um governo democratico, nem sempre esta associado as opinioes da maioria do povo.\n\n" +
                            "II. A democracia consensual e caracterizada pelo consenso a ser alcancado entre situacao e " +
                            "oposicao, nas decisoes governamentais.\n\nIII. Circunstancias politicas de polarizacao, marcadas " +
                            "pela alta competitividade e combatividade entre posicoes divergentes, caracterizam um modelo de " +
                            "democracia majoritaria.\n\nIV. Democracia consensual pressupoe que a situacao politica no poder " +
                            "considere em suas decisoes as necessidades das minorias, no sentido de governar para todo o povo." +
                            "\n\nE correto apenas o que se afirma em",
                    new String[]{
                            "I e II.",
                            "I e IV.",
                            "II e III.",
                            "I, III e IV.",
                            "II, III e IV."
                    },
                    3 // D
            }
    };

    @Override
    public void run(String... args) {

        //Verifica se ja existem no sistema.
        boolean cargaJaRodou = provaRepository.findAll().stream()
                .anyMatch(p -> p.getAno() == 2021 && p.getCurso().equals("Sistemas de Informação"));

        if (cargaJaRodou) {
            System.out.println("Carga inicial ignorada: prova de teste ja existe.");
            return;
        }

        Prova prova = provaRepository.save(new Prova(2021, "Sistemas de Informação"));

        for (int i = 0; i < QUESTOES.length; i++) {

            String enunciado = (String) QUESTOES[i][0];
            String[] alternativas = (String[]) QUESTOES[i][1];
            int indiceCorreta = (int) QUESTOES[i][2];

            Questao questao = questaoRepository.save(
                    new Questao(enunciado, Dificuldade.MEDIO, prova, null, i + 1));

            for (int j = 0; j < alternativas.length; j++) {
                char letra = (char) ('A' + j);
                boolean correta = (j == indiceCorreta);

                alternativaRepository.save(
                        new Alternativa(alternativas[j], correta, questao, letra));
            }
        }

        System.out.println("Carga inicial concluida: " + QUESTOES.length + " questoes reais criadas.");
    }
}