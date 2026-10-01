package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Prova;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.ProvaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// lê o PDF de uma prova e salva no banco a prova, as questões e as alternativas
@Service
public class ImportadorProvaServiceV2 {

    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final ProvaRepository provaRepository;

    // o Spring injeta os repositories pelo construtor
    public ImportadorProvaServiceV2(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, ProvaRepository provaRepository) {
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.provaRepository = provaRepository;
    }

    // importa o PDF e devolve quantas questões foram salvas
    public int provaMontada(String documento, int ano, String curso){

        // (?-i:A) desliga o case-insensitive pra pegar só a letra maiúscula
        // o lookahead no final para na próxima "QUESTÃO N" ou no fim do texto
        Pattern padraoQuestao = Pattern.compile("(?ius)quest[ãa]o\\s+(\\d+)(.*?)\\n(?-i:A)\\s(.*?)\\n(?-i:B)\\s(.*?)\\n(?-i:C)\\s(.*?)\\n(?-i:D)\\s(.*?)\\n(?-i:E)\\s(.*?)(?=quest[ãa]o\\s+\\d+|$)");

        String textoPdfCompleto = null;
        int questoesImportadas = 0;

        try {

            // acumula o texto já ajustado de todas as páginas
            String textoDessaPagina = "";
            File file = new File(documento);
            PDDocument doc = Loader.loadPDF(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            Pattern padrao = Pattern.compile("\\s{10,}"); // corredor entre colunas

            // título "QUESTÃO N" que às vezes sai no fim da página em vez do começo
            Pattern padraoTituloDeslocado = Pattern.compile("(?iu)quest[ãa]o\\s+\\d+\\s*$");

            // extrai o texto página por página, já separando as duas colunas
            for (int i = 1; i <= doc.getNumberOfPages(); i++){

                // limita o stripper só à página atual
                pdfTextStripper.setStartPage(i);
                pdfTextStripper.setEndPage(i);
                String bufferEsquerda = "";
                String bufferDireita = "";

                String textoDaPaginaAtual = pdfTextStripper.getText(doc);

                // recoloca o título deslocado na frente do texto da página
                Matcher matcherTitulo = padraoTituloDeslocado.matcher(textoDaPaginaAtual);
                boolean achouTitulo = matcherTitulo.find();
                if (achouTitulo) {
                    String tituloDeslocado = matcherTitulo.group();
                    String restoDoTexto = textoDaPaginaAtual.substring(0, matcherTitulo.start());
                    textoDaPaginaAtual = tituloDeslocado + "\n" + restoDoTexto;
                }

                String[] linha = textoDaPaginaAtual.split("\n");

                // quebra cada linha no corredor: o que fica antes vai para coluna esquerda, o que fica depois vai para a coluna da direita
                for (String linhaLinha : linha) {

                    Matcher matcher = padrao.matcher(linhaLinha);

                    if (matcher.find()){
                        int inicioCorredor = matcher.start();
                        int fimCorredor = matcher.end();

                        bufferEsquerda += linhaLinha.substring(0, inicioCorredor) + "\n";
                        bufferDireita += linhaLinha.substring(fimCorredor)+ "\n";
                    }
                    // linha sem corredor é de coluna única, vai inteira para esquerda
                    else {
                        bufferEsquerda += linhaLinha + "\n";
                    }
                }

                // reconstitui a leitura na ordem certa: coluna esquerda inteira, depois a direita
                textoDessaPagina += bufferEsquerda + bufferDireita;
            }

            textoPdfCompleto = textoDessaPagina;

            // salva a prova primeiro, as questões precisam dela como referência
            Prova prova = provaRepository.save(new Prova(ano, curso));

            Matcher matcherQuestao =  padraoQuestao.matcher(textoPdfCompleto);

            int questaoAtual = 0;

            // cada match é uma questão completa, com enunciado e as 5 alternativas
            while (matcherQuestao.find()){

                String numeroTexto = matcherQuestao.group(1);
                String enunciado = matcherQuestao.group(2);
                String alternativaA = matcherQuestao.group(3);
                String alternativaB = matcherQuestao.group(4);
                String alternativaC = matcherQuestao.group(5);
                String alternativaD = matcherQuestao.group(6);
                String alternativaE = matcherQuestao.group(7);


                int numeroQuestao = Integer.parseInt(numeroTexto);

                if (numeroQuestao <= questaoAtual) {
                    break;
                }

                questaoAtual = numeroQuestao;
                System.out.println(questaoAtual);

                // salva a questão antes para pegar o objeto com id e ligar nas alternativas
                Questao questaoSalva = questaoRepository.save(new Questao(enunciado, null, prova, null, numeroQuestao));


                alternativaRepository.save(new Alternativa(alternativaA, false, questaoSalva, 'A'));

                alternativaRepository.save(new Alternativa(alternativaB, false, questaoSalva, 'B'));

                alternativaRepository.save(new Alternativa(alternativaC, false, questaoSalva, 'C'));

                alternativaRepository.save(new Alternativa(alternativaD, false, questaoSalva, 'D'));

                alternativaRepository.save(new Alternativa(alternativaE, false, questaoSalva, 'E'));

                questoesImportadas++;
            }

            doc.close();
        }
        catch (IOException e) {
            System.out.println("Erro ao gerar PDF");
        }

        return questoesImportadas;
    }


}