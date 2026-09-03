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
    // Por enquanto separa o texto do PDF em blocos, um por questão objetiva, ignorando as discursivas.
    public int provaMontada(String documento, Tema tema){

        // Guarda o texto inteiro extraído do PDF. Declarada fora do try pra continuar visível depois dele.
        String retorno = null;

        // Controla o número da última questão objetiva processada, pra detectar quando a numeração
        // reinicia (sinal de que chegamos no Questionário de Percepção da Prova, que reusa os números 1-9).
        int numeroAtual = 1;

        // Flag que indica se já passamos pela primeira "QUESTÃO" válida do PDF.
        // Evita que o texto de introdução da prova (antes da questão 01) seja acumulado por engano.
        boolean primeiraQuestao = false;

        // Flag que indica se estamos "dentro" de uma questão discursiva no momento.
        // Enquanto for true, o conteúdo das linhas não deve ser acumulado (discursivas não são importadas).
        boolean discursiva = false;

        // Lista final: cada posição é o texto completo de uma questão objetiva já separada.
        List<String> enunuciado = new ArrayList<>();

        // Acumulador temporário: vai recebendo linha por linha o conteúdo da questão atual,
        // até encontrarmos o marcador da próxima questão e "fecharmos" esse bloco.
        String antiga = "";

        try {
            // Abre o arquivo PDF a partir do caminho recebido.
            File file = new File(documento);

            // Carrega o PDF em memória usando o PDFBox (Loader.loadPDF é o jeito certo na versão 3.x).
            PDDocument doc = Loader.loadPDF(file);

            // Ferramenta do PDFBox responsável por extrair o texto de dentro do PDF.
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            // Executa a extração e guarda o texto inteiro da prova numa única String.
            retorno = pdfTextStripper.getText(doc);

            // Quebra o texto em linhas. O PDF usa quebra de linha estilo Windows (\r\n),
            // então dividir só por "\n" deixaria um "\r" sobrando no fim de cada linha.
            String[] recebe = retorno.split("\r\n");

            // Percorre cada linha extraída do PDF, uma de cada vez.
            for (String linha : recebe) {

                // Primeiro checa se essa linha é o INÍCIO de uma questão discursiva.
                // Se for, liga a flag "discursiva" — a partir daqui o conteúdo não deve ser acumulado.
                if (linha.toLowerCase().startsWith("questão discursiva")) {
                    discursiva = true;
                }

                // Depois checa se essa linha é o marcador de uma questão OBJETIVA válida
                // (começa com "questão", mas não é "questão discursiva").
                if (linha.toLowerCase().startsWith("questão") && !linha.toLowerCase().contains("discursiva")) {

                    // Encontramos uma nova questão objetiva: desliga a flag de discursiva
                    // e liga a flag de "já passamos da primeira questão".
                    discursiva = false;
                    primeiraQuestao = true;

                    // Extrai o número da questão a partir da linha (ex: "QUESTÃO 09" -> "09").
                    // "QUESTÃO" tem 7 caracteres, então o número começa no índice 8 (7 + o espaço).
                    int numeroQuestao = Integer.parseInt(linha.substring(7).trim());

                    // Se o número dessa questão for MENOR que o número atual, a numeração reiniciou —
                    // isso significa que chegamos no Questionário de Percepção da Prova. Paramos aqui.
                    if (numeroAtual > numeroQuestao) {
                        if (!antiga.isEmpty()) {
                            enunuciado.add(antiga); // salva o último bloco acumulado antes de parar
                        }
                        break; // encerra o for, não processa mais nenhuma linha
                    }
                    // Caso contrário, é realmente a próxima questão da sequência normal.
                    else {
                        if (!antiga.isEmpty()) {
                            enunuciado.add(antiga); // fecha e guarda o bloco da questão anterior
                        }
                        antiga = ""; // zera o acumulador pra começar a juntar o conteúdo da nova questão
                        numeroAtual++; // avança o contador de controle
                    }

                }
                // Se a linha não é nenhum tipo de marcador de questão (nem discursiva, nem objetiva),
                // então é conteúdo normal (enunciado, alternativas, etc).
                else {
                    // Só acumula esse conteúdo se: já passamos da primeira questão E não estamos
                    // dentro de uma questão discursiva no momento.
                    if  (primeiraQuestao && !discursiva){
                        antiga += linha;
                    }
                }
            }

            // Imprime a lista final com todos os blocos de questões objetivas já separados.
            System.out.println(enunuciado.toString());

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