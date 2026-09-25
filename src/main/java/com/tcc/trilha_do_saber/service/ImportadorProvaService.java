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

@Service
public class ImportadorProvaService {

    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final ProvaRepository provaRepository;

    public ImportadorProvaService(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, ProvaRepository provaRepository) {
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.provaRepository = provaRepository;
    }

    public int provaMontada(String documento, int ano, String curso){

        String textoPdfCompleto = null;
        int questoesImportadas = 0;

        try {

            String textoDessaPagina = "";
            File file = new File(documento);
            PDDocument doc = Loader.loadPDF(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            Pattern padrao = Pattern.compile("\\s{10,}"); // corredor entre colunas

            // título "QUESTÃO N" que às vezes sai no fim da página em vez do começo
            Pattern padraoTituloDeslocado = Pattern.compile("(?iu)quest[ãa]o\\s+\\d+\\s*$");

            // extrai o texto página por página, já separando as duas colunas
            for (int i = 1; i <= doc.getNumberOfPages(); i++){

                pdfTextStripper.setStartPage(i);
                pdfTextStripper.setEndPage(i);
                String bufferEsquerda = "";
                String bufferDireita = "";

                String textoDaPaginaAtual = pdfTextStripper.getText(doc);

                System.out.println("=== PÁGINA " + i + " ===\n" + textoDaPaginaAtual);

                // recoloca o título deslocado na frente do texto da página
                Matcher matcherTitulo = padraoTituloDeslocado.matcher(textoDaPaginaAtual);
                boolean achouTitulo = matcherTitulo.find();
                System.out.println("Pagina " + i + " - achou titulo deslocado? " + achouTitulo);
                if (achouTitulo) {
                    String tituloDeslocado = matcherTitulo.group();
                    String restoDoTexto = textoDaPaginaAtual.substring(0, matcherTitulo.start());
                    textoDaPaginaAtual = tituloDeslocado + "\n" + restoDoTexto;
                }

                System.out.println("=== PÁGINA " + i + " DEPOIS DA CORRECAO ===\n" + textoDaPaginaAtual);

                // debug temporario: mostra o codigo unicode dos ultimos caracteres da pagina 34
                if (i == 34) {
                    String finalTrecho = textoDaPaginaAtual.substring(Math.max(0, textoDaPaginaAtual.length() - 25));
                    System.out.println("Ultimos caracteres da pagina 34:");
                    for (int c = 0; c < finalTrecho.length(); c++) {
                        char ch = finalTrecho.charAt(c);
                        System.out.println("'" + ch + "' -> U+" + String.format("%04X", (int) ch));
                    }
                }

                String[] linha = textoDaPaginaAtual.split("\n");

                // quebra cada linha no corredor: o que fica antes vai pra coluna
                // esquerda, o que fica depois vai pra coluna direita
                for (String linhaLinha : linha) {

                    Matcher matcher = padrao.matcher(linhaLinha);

                    if (matcher.find()){
                        int inicioCorredor = matcher.start();
                        int fimCorredor = matcher.end();

                        bufferEsquerda += linhaLinha.substring(0, inicioCorredor) + "\n";
                        bufferDireita += linhaLinha.substring(fimCorredor)+ "\n";
                    }
                    else {
                        bufferEsquerda += linhaLinha + "\n";
                    }
                }

                // reconstitui a leitura na ordem certa: coluna esquerda inteira, depois a direita
                textoDessaPagina += bufferEsquerda + bufferDireita;
            }

            textoPdfCompleto = textoDessaPagina;

            int posicaoAtual = 0;
            int posicaoAnterior = 0;
            int posicaoInicioProximaQuestao = 0;
            int numeroAtualQuestao = 1;

            Prova prova = provaRepository.save(new Prova(ano, curso));

            // varre o texto procurando os marcadores "\nA ", "\nB "... de cada questão
            proximaQuestao:
            while (true){

                char letraAtual = 'A';
                char proximaLetra = 'B';

                int indiceB = textoPdfCompleto.indexOf("\nB ", posicaoAtual);
                int indiceA = textoPdfCompleto.lastIndexOf("\nA ", indiceB);
                int posicaoBusca = indiceB;

                if(indiceB < 0){
                    break; // não tem mais nenhum "B" no texto, acabaram as questões
                }

                int posicaoCalculada = indiceB - posicaoAnterior;

                if (posicaoCalculada <= 500){
                    break; // "B" muito perto do anterior, provavelmente lixo no fim do documento
                }

                if (posicaoInicioProximaQuestao > indiceA){
                    // esse "A" achado é de antes da questão atual, é falso positivo: pula
                    posicaoAtual = indiceB + 1;
                    posicaoAnterior = indiceB;
                    numeroAtualQuestao++;
                    continue;
                }

                // enunciado é tudo entre o fim da questão anterior e o início da alternativa A
                String enunciado = textoPdfCompleto.substring(posicaoInicioProximaQuestao, indiceA);

                Questao questao = questaoRepository.save(new Questao(enunciado, null, prova, null, numeroAtualQuestao));

                // extrai as alternativas B, C, D (cada uma vai até o começo da próxima letra)
                while(proximaLetra <= 'E'){
                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    String buscaProximaLetra = "\n" + proximaLetra + " ";

                    int localizarSegundoIndice = textoPdfCompleto.indexOf(buscaProximaLetra, posicaoBusca);

                    if (localizarSegundoIndice < 0) {
                        // não achou a próxima letra, texto deve estar quebrado: descarta a questão inteira
                        System.out.println("Não encontrei a alternativa " + proximaLetra + " da questão " + numeroAtualQuestao + " — questão pulada.");
                        posicaoAtual = indiceB + 1;
                        posicaoAnterior = indiceB;
                        numeroAtualQuestao++;
                        continue proximaQuestao;
                    }

                    int localizarPrimeiroIndice = textoPdfCompleto.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);

                    String textoAlternativa = textoPdfCompleto.substring(localizarPrimeiroIndice, localizarSegundoIndice);
                    alternativaRepository.save(new Alternativa(textoAlternativa, false, questao, letraAtual));

                    letraAtual++;
                    proximaLetra++;
                    posicaoBusca = localizarSegundoIndice;
                }

                // alternativa E não tem uma "próxima letra" pra delimitar, então vai até a
                // próxima ocorrência de "questão" (ou até o fim do texto, se for a última)
                int comecaLetraE = textoPdfCompleto.toLowerCase().indexOf("questão", posicaoBusca);

                if (comecaLetraE < 0) {
                    comecaLetraE = textoPdfCompleto.length();
                }

                String textoAlternativaE = textoPdfCompleto.substring(posicaoBusca, comecaLetraE);
                alternativaRepository.save(new Alternativa(textoAlternativaE, false, questao, letraAtual));

                posicaoInicioProximaQuestao = comecaLetraE;
                posicaoAtual = indiceB + 1;
                posicaoAnterior = indiceB;

                questoesImportadas++;
                numeroAtualQuestao++;
            }

            doc.close();
        }
        catch (IOException e) {
            System.out.println("Erro ao gerar PDF");
        }

        return questoesImportadas;
    }
}