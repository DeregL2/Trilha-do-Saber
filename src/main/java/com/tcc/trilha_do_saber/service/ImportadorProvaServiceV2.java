package com.tcc.trilha_do_saber.service;

import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Prova;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.ProvaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.util.Matrix;
import java.awt.geom.Point2D;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ImportadorProvaServiceV2 {

    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final ProvaRepository provaRepository;

    private static final float ESCALA_RENDER = 2.0f;

    private static final float PADDING_PONTOS = 15f;

    public ImportadorProvaServiceV2(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, ProvaRepository provaRepository) {
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.provaRepository = provaRepository;
    }

    private static class CabecalhoQuestao {
        int numero;
        int pagina;
        boolean colunaEsquerda;
        float x;
        float y;
        float yFim;
    }

    private static class LocalizadorDeQuestoes extends PDFTextStripper {

        List<CabecalhoQuestao> cabecalhos = new ArrayList<>();
        Pattern padraoCabecalho = Pattern.compile("(?iu)^\\s*quest[ãa]o\\s+(\\d+)");

        public LocalizadorDeQuestoes() throws IOException {
            super();
            setSortByPosition(true);
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
            Matcher matcher = padraoCabecalho.matcher(string);

            if (matcher.find() && !textPositions.isEmpty()) {
                TextPosition primeiraPosicao = textPositions.get(0);
                float larguraPagina = getCurrentPage().getMediaBox().getWidth();

                CabecalhoQuestao cabecalho = new CabecalhoQuestao();
                cabecalho.numero = Integer.parseInt(matcher.group(1));
                cabecalho.pagina = getCurrentPageNo();
                cabecalho.x = primeiraPosicao.getXDirAdj();
                cabecalho.y = primeiraPosicao.getYDirAdj();
                cabecalho.colunaEsquerda = cabecalho.x < (larguraPagina / 2f);

                cabecalhos.add(cabecalho);
            }

            super.writeString(string, textPositions);
        }
    }

    private static class LocalizadorDeImagens extends PDFStreamEngine {

        Map<Integer, List<float[]>> imagensPorPagina = new HashMap<>();
        private int paginaAtual;

        LocalizadorDeImagens() {
            addOperator(new Concatenate(this));
            addOperator(new Save(this));
            addOperator(new Restore(this));
            addOperator(new SetGraphicsStateParameters(this));
        }

        void processarPagina(PDPage pagina, int numeroPagina) throws IOException {
            this.paginaAtual = numeroPagina;
            processPage(pagina);
        }

        @Override
        protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
            String nomeOperador = operator.getName();

            if ("Do".equals(nomeOperador) && !operands.isEmpty()) {
                COSName nome = (COSName) operands.get(0);
                PDXObject xobject = getResources().getXObject(nome);

                if (xobject instanceof PDImageXObject) {
                    registrarFaixaDaImagemAtual();
                } else if (xobject instanceof PDFormXObject) {
                    showForm((PDFormXObject) xobject);
                }
            }

            super.processOperator(operator, operands);
        }

        private void registrarFaixaDaImagemAtual() {
            Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();

            float yMinBaixoParaCima = Float.MAX_VALUE;
            float yMaxBaixoParaCima = -Float.MAX_VALUE;

            float[][] cantosDoQuadrado = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};
            for (float[] canto : cantosDoQuadrado) {
                Point2D.Float pontoTransformado = ctm.transformPoint(canto[0], canto[1]);
                yMinBaixoParaCima = Math.min(yMinBaixoParaCima, pontoTransformado.y);
                yMaxBaixoParaCima = Math.max(yMaxBaixoParaCima, pontoTransformado.y);
            }

            float alturaPagina = getCurrentPage().getMediaBox().getHeight();
            float yTopo = alturaPagina - yMaxBaixoParaCima;
            float yBase = alturaPagina - yMinBaixoParaCima;

            imagensPorPagina.computeIfAbsent(paginaAtual, k -> new ArrayList<>())
                    .add(new float[]{yTopo, yBase});
        }
    }

    public int provaMontada(String documento, int ano, String curso){

        Pattern padraoQuestao = Pattern.compile("(?ius)quest[ãa]o\\s+(\\d+)((?:(?!quest[ãa]o\\s+\\d+).){100,})\\n(?-i:A)\\s+((?:(?!quest[ãa]o\\s+\\d+).)*?)\\n(?-i:B)\\s+((?:(?!quest[ãa]o\\s+\\d+).)*?)\\n(?-i:C)\\s+((?:(?!quest[ãa]o\\s+\\d+).)*?)\\n(?-i:D)\\s+((?:(?!quest[ãa]o\\s+\\d+).)*?)\\n(?-i:E)\\s+(.*?)(?=quest[ãa]o\\s+\\d+|[áa]rea\\s+livre|\\*[\\d*]*R\\s*\\d+|$)");

        String textoPdfCompleto = null;
        int questoesImportadas = 0;

        try {

            String textoDessaPagina = "";
            File file = new File(documento);
            PDDocument doc = Loader.loadPDF(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            Pattern padrao = Pattern.compile("\\s{10,}");

            Pattern padraoTituloDeslocado = Pattern.compile("(?iu)quest[ãa]o\\s+\\d+\\s*$");

            for (int i = 1; i <= doc.getNumberOfPages(); i++){

                pdfTextStripper.setStartPage(i);
                pdfTextStripper.setEndPage(i);
                String bufferEsquerda = "";
                String bufferDireita = "";

                String textoDaPaginaAtual = pdfTextStripper.getText(doc);

                Matcher matcherTitulo = padraoTituloDeslocado.matcher(textoDaPaginaAtual);
                boolean achouTitulo = matcherTitulo.find();
                if (achouTitulo) {
                    String tituloDeslocado = matcherTitulo.group();
                    String restoDoTexto = textoDaPaginaAtual.substring(0, matcherTitulo.start());
                    textoDaPaginaAtual = tituloDeslocado + "\n" + restoDoTexto;
                }

                String[] linha = textoDaPaginaAtual.split("\n");

                for (String linhaLinha : linha) {

                    String linhaSemEspacos = linhaLinha.trim();
                    if (linhaSemEspacos.matches("(?i).*\\*[\\d*]*R\\s*\\d{5,}.*")
                            || linhaSemEspacos.matches("(?i)\\d*\\s*sistemas\\s+de\\s+informa.*")) {
                        continue;
                    }

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

                textoDessaPagina += bufferEsquerda + bufferDireita;
            }

            textoPdfCompleto = textoDessaPagina
                    .replace("tEXtO ii", "TEXTO II")
                    .replace("tEXtO i", "TEXTO I");

            LocalizadorDeQuestoes localizador = new LocalizadorDeQuestoes();
            localizador.setStartPage(1);
            localizador.setEndPage(doc.getNumberOfPages());
            localizador.getText(doc);

            List<CabecalhoQuestao> cabecalhos = localizador.cabecalhos;

            cabecalhos.sort((a, b) -> {
                if (a.pagina != b.pagina) return Integer.compare(a.pagina, b.pagina);
                if (a.colunaEsquerda != b.colunaEsquerda) return a.colunaEsquerda ? -1 : 1;
                return Float.compare(a.y, b.y);
            });

            List<CabecalhoQuestao> cabecalhosValidos = new ArrayList<>();
            int ultimoNumeroValido = 0;
            for (CabecalhoQuestao c : cabecalhos) {
                if (c.numero <= ultimoNumeroValido) {
                    break;
                }
                ultimoNumeroValido = c.numero;
                cabecalhosValidos.add(c);
            }
            cabecalhos = cabecalhosValidos;

            for (int i = 0; i < cabecalhos.size(); i++) {
                CabecalhoQuestao atual = cabecalhos.get(i);
                float alturaPagina = doc.getPage(atual.pagina - 1).getMediaBox().getHeight();

                boolean temProximoNaMesmaColuna = i + 1 < cabecalhos.size()
                        && cabecalhos.get(i + 1).pagina == atual.pagina
                        && cabecalhos.get(i + 1).colunaEsquerda == atual.colunaEsquerda;

                if (temProximoNaMesmaColuna) {
                    atual.yFim = cabecalhos.get(i + 1).y;
                } else {
                    atual.yFim = alturaPagina;
                }
            }

            Map<Integer, CabecalhoQuestao> cabecalhoPorNumero = new HashMap<>();
            for (CabecalhoQuestao c : cabecalhos) {
                cabecalhoPorNumero.put(c.numero, c);
            }

            Map<Integer, BufferedImage> cachePaginas = new HashMap<>();
            PDFRenderer renderer = new PDFRenderer(doc);

            LocalizadorDeImagens localizadorImagens = new LocalizadorDeImagens();
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                localizadorImagens.processarPagina(doc.getPage(i - 1), i);
            }
            Map<Integer, List<float[]>> imagensPorPagina = localizadorImagens.imagensPorPagina;

            Prova prova = provaRepository.save(new Prova(ano, curso));

            Matcher matcherQuestao =  padraoQuestao.matcher(textoPdfCompleto);

            int questaoAtual = 0;

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

                byte[] imagemRecortada = null;
                CabecalhoQuestao cabecalho = cabecalhoPorNumero.get(numeroQuestao);
                float[] faixaImagem = cabecalho != null ? faixaDaImagemNaQuestao(imagensPorPagina, cabecalho) : null;

                if (faixaImagem != null) {
                    imagemRecortada = recortarImagemDaQuestao(renderer, cachePaginas, cabecalho, faixaImagem);
                }

                Questao questaoSalva = questaoRepository.save(new Questao(enunciado, null, prova, null, numeroQuestao, imagemRecortada));

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

    private float[] faixaDaImagemNaQuestao(Map<Integer, List<float[]>> imagensPorPagina, CabecalhoQuestao cabecalho) {
        List<float[]> imagensDaPagina = imagensPorPagina.get(cabecalho.pagina);
        if (imagensDaPagina == null) {
            return null;
        }

        float yTopo = Float.MAX_VALUE;
        float yBase = -Float.MAX_VALUE;
        boolean encontrou = false;

        for (float[] faixaImagem : imagensDaPagina) {
            boolean sobrepoe = faixaImagem[0] < cabecalho.yFim && faixaImagem[1] > cabecalho.y;
            if (sobrepoe) {
                encontrou = true;
                yTopo = Math.min(yTopo, faixaImagem[0]);
                yBase = Math.max(yBase, faixaImagem[1]);
            }
        }

        return encontrou ? new float[]{yTopo, yBase} : null;
    }

    private byte[] recortarImagemDaQuestao(PDFRenderer renderer, Map<Integer, BufferedImage> cachePaginas, CabecalhoQuestao cabecalho, float[] faixaImagem) throws IOException {

        BufferedImage imagemPagina = cachePaginas.get(cabecalho.pagina);
        if (imagemPagina == null) {
            imagemPagina = renderer.renderImage(cabecalho.pagina - 1, ESCALA_RENDER);
            cachePaginas.put(cabecalho.pagina, imagemPagina);
        }

        int larguraImagem = imagemPagina.getWidth();
        int alturaImagem = imagemPagina.getHeight();

        int xInicio = 0;
        int largura = larguraImagem;

        int yInicio = Math.round((faixaImagem[0] - PADDING_PONTOS) * ESCALA_RENDER);
        int yFim = Math.round((faixaImagem[1] + PADDING_PONTOS) * ESCALA_RENDER);

        yInicio = Math.max(0, yInicio);
        yFim = Math.min(alturaImagem, yFim);

        int altura = yFim - yInicio;

        if (altura <= 0) {
            return null;
        }

        BufferedImage recorte = imagemPagina.getSubimage(xInicio, yInicio, largura, altura);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(recorte, "png", baos);
        return baos.toByteArray();
    }
}