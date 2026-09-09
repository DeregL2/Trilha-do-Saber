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

@Service // Anotação do Spring informando que nessa Classe contem lógica de negócio e gerencia ela como um bean.
public class ImportadorProvaService {

    // Repositories usados mais pra frente, pra salvar Questao e Alternativa extraídas do PDF.
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;

    // Construtor da Classe
    public ImportadorProvaService(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository) {

        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;

    }

    // Metodo principal do importador.
    public int provaMontada(String documento, Tema tema){

        // Texto inteiro extraído do PDF.
        String textoPdfCompleto = null;

        // Controla o número da última questão objetiva processada.
        int numeroQuestaoEsperado = 1;

        // Se já passamos da primeira "QUESTÃO" válida do PDF.
        boolean jaComecouQuestao = false;

        // Se estamos dentro de uma questão discursiva no momento.
        boolean emQuestaoDiscursiva = false;

        // Cada posição é o bloco completo de uma questão objetiva já separada.
        List<String> blocosDeQuestoes = new ArrayList<>();

        // Acumulador temporário do bloco da questão atual.
        String blocoAtual = "";

        try {
            // Abre o arquivo PDF a partir do caminho recebido.
            File file = new File(documento);

            // Carrega o PDF em memória (jeito certo na versão 3.x do PDFBox).
            PDDocument doc = Loader.loadPDF(file);

            // Ferramenta do PDFBox que extrai o texto de dentro do PDF.
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            // Executa a extração e guarda o texto inteiro da prova.
            textoPdfCompleto = pdfTextStripper.getText(doc);

            // Quebra em linhas (o PDF usa quebra de linha estilo Windows, \r\n).
            String[] linhasDoPdf = textoPdfCompleto.split("\r\n");

            // Separar o PDF em blocos por questão
//            for (String linha : linhasDoPdf) {
//
//                // Início de uma questão discursiva: liga a flag, não acumula o conteúdo dela.
//                if (linha.toLowerCase().startsWith("questão discursiva")) {
//                    emQuestaoDiscursiva = true;
//                }
//
//                // Marcador de questão OBJETIVA válida (começa com "questão", mas não é discursiva).
//                if (linha.toLowerCase().startsWith("questão") && !linha.toLowerCase().contains("discursiva")) {
//
//                    emQuestaoDiscursiva = false;
//                    jaComecouQuestao = true;
//
//                    // Extrai o número da questão (ex: "QUESTÃO 09" -> "09").
//                    int numeroQuestao = Integer.parseInt(linha.substring(7).trim());
//
//                    if (numeroQuestaoEsperado > numeroQuestao) {
//                        if (!blocoAtual.isEmpty()) {
//                            blocosDeQuestoes.add(blocoAtual);
//                        }
//                        break;
//                    }
//                    // Próxima questão da sequência normal: fecha o bloco anterior e abre um novo.
//                    else {
//                        if (!blocoAtual.isEmpty()) {
//                            blocosDeQuestoes.add(blocoAtual);
//                        }
//                        blocoAtual = "";
//                        numeroQuestaoEsperado++;
//                    }
//
//                }
//
//                else {
//                    // Só acumula se já passamos da primeira questão e não estamos numa discursiva.
//                    if  (jaComecouQuestao && !emQuestaoDiscursiva){
//                        blocoAtual += linha + "\n";
//                    }
//                }
//            }
//
//            // Separar cada bloco em enunciado + 5 alternativas
//            for (String blocoQuestao : blocosDeQuestoes){
//
//                // As 5 alternativas dessa questão, na ordem A-E.
//                List<String> alternativas = new ArrayList<>();
//
//                char letraAtual = 'A';
//                char proximaLetra = 'B';
//
//                // Normaliza espaços repetidos (o PDFBox extrai algumas páginas com 1 espaço após a letra, outras com 2).
//                blocoQuestao = blocoQuestao.replaceAll(" +", " ");
//
//                // Posição do marcador da alternativa B, ancorado com "\n".
//                int indiceB = blocoQuestao.indexOf("\nB ");
//
//                // Posição do marcador da alternativa A. lastIndexOf até indiceB
//                int indiceA = blocoQuestao.lastIndexOf("\nA ", indiceB);
//
//                // Não achou A ou B (ex: questão com alternativas em formato de tabela) — pula.
//                if (indiceA < 0 || indiceB < 0){
//                    System.out.println(blocoQuestao);
//                    continue;
//                }
//
//                // Enunciado é tudo que vem antes do marcador da alternativa A.
//                String enunciado = blocoQuestao.substring(0, indiceA);
//                System.out.print(enunciado);
//
//
//                while (proximaLetra <= 'E'){
//
//                    String buscaLetraAtual = "\n" + letraAtual + " ";
//                    String buscaProximaLetra = "\n" + proximaLetra + " ";
//
//                    // Acha a próxima letra primeiro, depois a atual (lastIndexOf, limitado até a próxima).
//                    int localizarSegundoIndice = blocoQuestao.indexOf(buscaProximaLetra);
//                    int localizarPrimeiroInidice = blocoQuestao.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);
//
//                    // Texto da alternativa atual: tudo entre essas duas posições.
//                    String novoTextoAlternativa = blocoQuestao.substring(localizarPrimeiroInidice, localizarSegundoIndice);
//                    alternativas.add(novoTextoAlternativa);
//
//                    letraAtual++;
//                    proximaLetra++;
//
//                }
//
//                // A última alternativa (E) não tem "próxima letra" para servir de limite trata à parte.
//                if (letraAtual == 'E'){
//                    String buscaLetraAtual = "\n" + letraAtual + " ";
//                    int localizarPrimeiroInidice = blocoQuestao.indexOf(buscaLetraAtual);
//
//                    // Do início da alternativa E até o fim do bloco.
//                    String novoTextoAlternativa = blocoQuestao.substring(localizarPrimeiroInidice);
//                    alternativas.add(novoTextoAlternativa);
//                }
//
//                // Confere visualmente que as 5 alternativas saíram certas.
//                System.out.println(alternativas);
//
//            }
            int posicaoAtual = 0;
            int posicaoAnterior = 0;

            while (true){

                char letraAtual = 'A';
                char proximaLetra = 'B';
                int indiceB = textoPdfCompleto.indexOf("\nB ", posicaoAtual);
                int indiceA = textoPdfCompleto.lastIndexOf("\nA ", indiceB);
                int posicaoBusca = indiceB;

                if(indiceB < 0){
                    break;
                }

                int posicaoCalculada = indiceB - posicaoAnterior;

                if (posicaoCalculada <= 500){
                    break;
                }

                // Processa os pares de letras (A-B, B-C, C-D, D-E), exceto a última (E).
                while(proximaLetra <= 'E'){

                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    String buscaProximaLetra = "\n" + proximaLetra + " ";

                    // Acha a próxima letra primeiro, depois a atual (lastIndexOf, limitado até a próxima).
                    int localizarSegundoIndice = textoPdfCompleto.indexOf(buscaProximaLetra, posicaoBusca);
                    int localizarPrimeiroInidice = textoPdfCompleto.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);

                    letraAtual ++;
                    proximaLetra ++;
                    posicaoBusca = localizarSegundoIndice;

                    System.out.println(localizarPrimeiroInidice);

                }


                System.out.println(indiceB);
                System.out.println(indiceA);
                posicaoAtual = indiceB + 1;
                posicaoAnterior = indiceB;
            }

            // Libera o documento da memória.
            doc.close();
        }
        catch (IOException e) {
            System.out.println("Erro ao gerar PDF");
        }

        return 0;
    }

}
