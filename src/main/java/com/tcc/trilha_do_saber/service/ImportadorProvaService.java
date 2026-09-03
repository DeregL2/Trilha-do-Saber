package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Tema;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service // Anotação do Spring informando que essa Classe contem lógica de negocio e gerencia ela como um bean.
public class ImportadorProvaService {

    // Repositories que essa service vai usar mais pra frente, pra salvar as Questao e Alternativa extraídas do PDF.
    // Ainda não são usados dentro do metodo, mas já ficam injetados prontos pra quando a lógica de salvar for escrita.
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;



    // Construtor da Classe
    // O Spring chama esse construtor automaticamente ao criar o bean, injetando as implementações
    // dos Repositories (ele gerencia essas instâncias, por isso não usamos "new" aqui).
    public ImportadorProvaService(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository) {

        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;

    }

    // Metodo principal do importador.
    // Recebe o caminho do PDF (documento) e o Tema/curso ao qual essa prova pertence (ainda não usado).
    // Etapa 1: separa o texto do PDF em blocos, um por questão objetiva, ignorando as discursivas.
    // Etapa 2: separa cada bloco em enunciado + 5 alternativas (A a E).
    public int provaMontada(String documento, Tema tema){

        // Guarda o texto inteiro extraído do PDF. Declarada fora do try pra continuar visível depois dele.
        String textoPdfCompleto = null;

        // Controla o número da última questão objetiva processada, pra detectar quando a numeração
        // reinicia (sinal de que chegamos no Questionário de Percepção da Prova, que reusa os números 1-9).
        int numeroQuestaoEsperado = 1;

        // Flag que indica se já passamos pela primeira "QUESTÃO" válida do PDF.
        // Evita que o texto de introdução da prova (antes da questão 01) seja acumulado por engano.
        boolean jaComecouQuestao = false;

        // Flag que indica se estamos "dentro" de uma questão discursiva no momento.
        // Enquanto for true, o conteúdo das linhas não deve ser acumulado (discursivas não são importadas).
        boolean emQuestaoDiscursiva = false;

        // Lista final: cada posição é o bloco de texto completo de uma questão objetiva já separada
        // (enunciado + alternativas juntos, ainda sem a separação da Etapa 2).
        List<String> blocosDeQuestoes = new ArrayList<>();

        // Acumulador temporário: vai recebendo linha por linha o conteúdo da questão atual,
        // até encontrarmos o marcador da próxima questão e "fecharmos" esse bloco.
        String blocoAtual = "";

        try {
            // Abre o arquivo PDF a partir do caminho recebido.
            File file = new File(documento);

            // Carrega o PDF em memória usando o PDFBox (Loader.loadPDF é o jeito certo na versão 3.x).
            PDDocument doc = Loader.loadPDF(file);

            // Ferramenta do PDFBox responsável por extrair o texto de dentro do PDF.
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            // Executa a extração e guarda o texto inteiro da prova numa única String.
            textoPdfCompleto = pdfTextStripper.getText(doc);

            // Quebra o texto em linhas. O PDF usa quebra de linha estilo Windows (\r\n),
            // então dividir só por "\n" deixaria um "\r" sobrando no fim de cada linha.
            String[] linhasDoPdf = textoPdfCompleto.split("\r\n");

            // ===================== ETAPA 1: separar o PDF em blocos por questão =====================
            // Percorre cada linha extraída do PDF, uma de cada vez.
            for (String linha : linhasDoPdf) {

                // Primeiro checa se essa linha é o INÍCIO de uma questão discursiva.
                // Se for, liga a flag "emQuestaoDiscursiva" — a partir daqui o conteúdo não deve ser acumulado.
                if (linha.toLowerCase().startsWith("questão discursiva")) {
                    emQuestaoDiscursiva = true;
                }

                // Depois checa se essa linha é o marcador de uma questão OBJETIVA válida
                // (começa com "questão", mas não é "questão discursiva").
                if (linha.toLowerCase().startsWith("questão") && !linha.toLowerCase().contains("discursiva")) {

                    // Encontramos uma nova questão objetiva: desliga a flag de discursiva
                    // e liga a flag de "já passamos da primeira questão".
                    emQuestaoDiscursiva = false;
                    jaComecouQuestao = true;

                    // Extrai o número da questão a partir da linha (ex: "QUESTÃO 09" -> "09").
                    // "QUESTÃO" tem 7 caracteres, então o número começa no índice 8 (7 + o espaço).
                    int numeroQuestao = Integer.parseInt(linha.substring(7).trim());

                    // Se o número dessa questão for MENOR que o número esperado, a numeração reiniciou —
                    // isso significa que chegamos no Questionário de Percepção da Prova. Paramos aqui.
                    if (numeroQuestaoEsperado > numeroQuestao) {
                        if (!blocoAtual.isEmpty()) {
                            blocosDeQuestoes.add(blocoAtual); // salva o último bloco acumulado antes de parar
                        }
                        break; // encerra o for, não processa mais nenhuma linha
                    }
                    // Caso contrário, é realmente a próxima questão da sequência normal.
                    else {
                        if (!blocoAtual.isEmpty()) {
                            blocosDeQuestoes.add(blocoAtual); // fecha e guarda o bloco da questão anterior
                        }
                        blocoAtual = ""; // zera o acumulador pra começar a juntar o conteúdo da nova questão
                        numeroQuestaoEsperado++; // avança o contador de controle
                    }

                }
                // Se a linha não é nenhum tipo de marcador de questão (nem discursiva, nem objetiva),
                // então é conteúdo normal (enunciado, alternativas, etc).
                else {
                    // Só acumula esse conteúdo se: já passamos da primeira questão E não estamos
                    // dentro de uma questão discursiva no momento.
                    if  (jaComecouQuestao && !emQuestaoDiscursiva){
                        blocoAtual += linha + "\n";
                    }
                }
            }

            // ============ ETAPA 2: separar cada bloco em enunciado + 5 alternativas ============
            // Percorre cada bloco de questão já pronto (vindo da Etapa 1).
            for (String blocoQuestao : blocosDeQuestoes){

                // Lista que vai guardar as 5 alternativas dessa questão específica, na ordem A-E.
                // Recriada a cada volta do for, pra não misturar alternativas de questões diferentes.
                List<String> alternativas = new ArrayList<>();

                // Variáveis de controle do loop de letras: "letra atual" e "próxima letra".
                // Também recriadas a cada questão, pra sempre começar do zero em A/B.
                char letraAtual = 'A';
                char proximaLetra = 'B';

                // Normaliza os espaços do bloco: qualquer sequência de espaços repetidos vira um espaço só.
                // Isso é necessário porque o PDFBox extrai algumas páginas com 1 espaço após a letra da
                // alternativa, e outras com 2 espaços — sem essa normalização, o padrão de busca não seria
                // consistente pra prova inteira.
                blocoQuestao = blocoQuestao.replaceAll(" +", " ");

                // Acha a posição do marcador da alternativa B (ancorado com "\n" pra não confundir com
                // um "B" que apareça, por acaso, no meio do enunciado).
                int indiceB = blocoQuestao.indexOf("\nB ");

                // Acha a posição do marcador da alternativa A. Usamos lastIndexOf (busca de trás pra
                // frente) limitado até "indiceB", em vez de indexOf simples, porque "A" sozinho é uma
                // palavra comum em português (artigo definido) — várias frases do ENUNCIADO podem começar
                // com "A " por coincidência. Pegando a ÚLTIMA ocorrência de "\nA " antes do "B" real,
                // garantimos que estamos pegando o "A" que é realmente o marcador da alternativa.
                int indiceA = blocoQuestao.lastIndexOf("\nA ", indiceB);

                // Se não achou A ou B nesse bloco (caso da questão com alternativas em formato de tabela,
                // que tem um formato totalmente diferente), pula essa questão e segue pra próxima.
                if (indiceA < 0 || indiceB < 0){
                    System.out.println(blocoQuestao);
                    continue;
                }

                // O enunciado é tudo que vem ANTES do marcador da alternativa A.
                String enunciado = blocoQuestao.substring(0, indiceA);
                System.out.print(enunciado);

                // Loop principal: processa os pares de letras (A-B, B-C, C-D, D-E), extraindo o texto
                // de cada alternativa (exceto a E, que é tratada depois, separadamente).
                // Continua enquanto "próximaLetra" ainda for uma letra válida (até E, inclusive).
                while (proximaLetra <= 'E'){

                    // Monta os textos de busca a partir dos chars (concatenação converte char em String).
                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    String buscaProximaLetra = "\n" + proximaLetra + " ";

                    // Acha a posição da PRÓXIMA letra primeiro (busca normal, do início pro fim).
                    int localizarSegundoIndice = blocoQuestao.indexOf(buscaProximaLetra);

                    // Acha a posição da letra ATUAL usando lastIndexOf, limitado até a posição da
                    // próxima letra — mesma lógica de segurança usada acima pro "A", agora aplicada
                    // a QUALQUER letra do loop, pra evitar pegar uma ocorrência errada em outro lugar do texto.
                    int localizarPrimeiroInidice = blocoQuestao.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);

                    // O texto da alternativa atual é tudo que fica ENTRE essas duas posições.
                    String novoTextoAlternativa = blocoQuestao.substring(localizarPrimeiroInidice, localizarSegundoIndice);

                    // Guarda essa alternativa na lista da questão.
                    alternativas.add(novoTextoAlternativa);

                    // Avança as duas letras de controle pra próxima volta (B->C, C->D, D->E).
                    letraAtual++;
                    proximaLetra++;

                }

                // Depois que o while termina, "letraAtual" ficou valendo 'E' — a última alternativa
                // ainda não foi extraída, porque ela não tem uma "próxima letra" (F) pra servir de limite.
                if (letraAtual == 'E'){
                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    int localizarPrimeiroInidice = blocoQuestao.indexOf(buscaLetraAtual);

                    // Pega tudo do início da alternativa E até o FIM do bloco (substring de um argumento só).
                    String novoTextoAlternativa = blocoQuestao.substring(localizarPrimeiroInidice);
                    alternativas.add(novoTextoAlternativa);
                }

                // Imprime as 5 alternativas dessa questão, só pra conferir visualmente que saíram certas.
                System.out.println(alternativas);

            }

            // Libera o documento da memória depois de terminar de usá-lo.
            doc.close();
        }
        catch (IOException e) {
            // Loader.loadPDF e getText podem lançar IOException (exceção checada),
            // então precisamos tratar isso. Por enquanto só avisamos no console.
            System.out.println("Erro ao gerar PDF");
        }



        // Retorno temporário, só pra o metodo compilar. Vai virar a contagem real
        // de questões importadas com sucesso quando a lógica de parsing estiver pronta.
        return 0;
    }

}