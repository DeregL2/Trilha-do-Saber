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
import java.util.ArrayList;
import java.util.List;

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

            // Cria e salva a Prova (o PDF inteiro) uma única vez, antes do loop.
            Prova prova = provaRepository.save(new Prova(ano, curso));

            while (true){

                char letraAtual = 'A';
                char proximaLetra = 'B';

                // Acha o primeiro B, a partir dele, o procura o A mais proximo (fim do enunciado / início das alternativas).
                int indiceB = textoPdfCompleto.indexOf("\nB ", posicaoAtual);
                int indiceA = textoPdfCompleto.lastIndexOf("\nA ", indiceB);
                int posicaoBusca = indiceB;
                List<String> alternativas = new ArrayList<>();

                // Não achou mais nenhum "B "; acabaram as questões objetivas.
                if(indiceB < 0){
                    break;
                }

                // Distância pequena demais para ser uma questão nova de verdade (provável fim do PDF).
                int posicaoCalculada = indiceB - posicaoAnterior;
                if (posicaoCalculada <= 500){
                    break;
                }

                // Pula a questão problemática (ex: Q29->Q30) para não estourar o substring.
                if (posicaoInicioProximaQuestao > indiceA){
                    posicaoAtual = indiceB + 1;
                    posicaoAnterior = indiceB;
                    System.out.println(posicaoInicioProximaQuestao);
                    continue;
                }

                // Enunciado vai do fim da questão anterior até o início da alternativa A.
                String enunciado = textoPdfCompleto.substring(posicaoInicioProximaQuestao, indiceA);

                // Salva a Questao já vinculada à Prova e ao Tema.
                Questao questao = questaoRepository.save(new Questao(enunciado, null, tema, prova, null));

                // Processa os pares de letras (A-B, B-C, C-D, D-E), recortando o texto entre uma letra e a próxima.
                while(proximaLetra <= 'E'){
                    String buscaLetraAtual = "\n" + letraAtual + " ";
                    String buscaProximaLetra = "\n" + proximaLetra + " ";

                    int localizarSegundoIndice = textoPdfCompleto.indexOf(buscaProximaLetra, posicaoBusca);
                    int localizarPrimeiroIndice = textoPdfCompleto.lastIndexOf(buscaLetraAtual, localizarSegundoIndice);

                    letraAtual ++;
                    proximaLetra ++;
                    posicaoBusca = localizarSegundoIndice;

                    // Salva a alternativa (A a D) vinculada à Questao;
                    String textoAlternativa = textoPdfCompleto.substring(localizarPrimeiroIndice, localizarSegundoIndice);
                    alternativas.add(textoAlternativa);
                    alternativaRepository.save(new Alternativa(textoAlternativa, false, questao));
                }

                // Alternativa E vai até o início da próxima "questão" no texto (mesmo ponto que fecha o enunciado seguinte).
                int comecaLetraE = textoPdfCompleto.toLowerCase().indexOf("questão", posicaoBusca);
                String textoAlternativaE = textoPdfCompleto.substring(posicaoBusca, comecaLetraE);
                alternativas.add(textoAlternativaE);
                alternativaRepository.save(new Alternativa(textoAlternativaE, false, questao));

                // Guarda onde a próxima questão deve começar a ler o enunciado.
                posicaoInicioProximaQuestao = comecaLetraE;
                posicaoAtual = indiceB + 1;
                posicaoAnterior = indiceB;

                // Só conta se a questão inteira foi processada com sucesso (não caiu no guard acima).
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