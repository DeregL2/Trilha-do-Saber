package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Prova;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Tema;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.ProvaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ImportadorProvaService {

    // Repositories para persistir Prova, Questao e Alternativa.
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final ProvaRepository provaRepository;

    public ImportadorProvaService(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, ProvaRepository provaRepository) {
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.provaRepository = provaRepository;
    }

    // Lê o PDF, separa cada questão/alternativa e salva no banco. Retorna quantas questões foram importadas.
    public int provaMontada(String documento, Tema tema, int ano, String curso){

        String textoPdfCompleto = null;
        int questoesImportadas = 0;

        try {
            File file = new File(documento);
            PDDocument doc = Loader.loadPDF(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            textoPdfCompleto = pdfTextStripper.getText(doc);

            int posicaoAtual = 0;
            int posicaoAnterior = 0;
            int posicaoInicioProximaQuestao = 0;
            int numeroAtualQuestao = 1;

            Prova prova = provaRepository.save(new Prova(ano, curso));

            proximaQuestao:
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

                if (posicaoInicioProximaQuestao > indiceA){
                    posicaoAtual = indiceB + 1;
                    posicaoAnterior = indiceB;
                    numeroAtualQuestao++;
                    continue;
                }

                String enunciado = textoPdfCompleto.substring(posicaoInicioProximaQuestao, indiceA);

                Questao questao = questaoRepository.save(new Questao(enunciado, null, tema, prova, null, numeroAtualQuestao));

                while(proximaLetra <= 'E'){
                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    String buscaProximaLetra = "\n" + proximaLetra + " ";

                    int localizarSegundoIndice = textoPdfCompleto.indexOf(buscaProximaLetra, posicaoBusca);


                    if (localizarSegundoIndice < 0) {
                        System.out.println("Não encontrei a alternativa " + proximaLetra + " da questão " + numeroAtualQuestao + " — questão pulada.");
                        posicaoAtual = indiceB + 1;
                        posicaoAnterior = indiceB;
                        numeroAtualQuestao++;
                        continue proximaQuestao;
                    }

                    int localizarPrimeiroIndice = textoPdfCompleto.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);

                    String textoAlternativa = textoPdfCompleto.substring(localizarPrimeiroIndice, localizarSegundoIndice);
                    alternativaRepository.save(new Alternativa(textoAlternativa, false, questao,letraAtual));

                    letraAtual ++;
                    proximaLetra ++;
                    posicaoBusca = localizarSegundoIndice;
                }

                int comecaLetraE = textoPdfCompleto.toLowerCase().indexOf("questão", posicaoBusca);

                if (comecaLetraE < 0) {
                    comecaLetraE = textoPdfCompleto.length();
                }

                String textoAlternativaE = textoPdfCompleto.substring(posicaoBusca, comecaLetraE);

                alternativaRepository.save(new Alternativa(textoAlternativaE, false, questao,letraAtual));

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