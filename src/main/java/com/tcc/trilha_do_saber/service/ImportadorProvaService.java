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
    // Por enquanto só lê o PDF e imprime linha por linha, pra validar a extração antes de montar a lógica de verdade.
    public int provaMontada(String documento, Tema tema){

        // Guarda o texto inteiro extraído do PDF. Declarada fora do try pra continuar visível depois dele.
        String retorno = null;

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

            // Percorre cada linha extraída e imprime no console, só pra conferir visualmente
            // se a extração e a quebra de linhas estão saindo como esperado.
            for (String linha : recebe) {
                System.out.printf("linha %s.\n", linha);
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