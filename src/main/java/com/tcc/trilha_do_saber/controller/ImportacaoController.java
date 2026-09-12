package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.service.GabaritoService;
import com.tcc.trilha_do_saber.service.ImportadorProvaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class ImportacaoController {

    private final ImportadorProvaService importadorProvaService;
    private final GabaritoService gabaritoService;

    // Construtor
    public ImportacaoController(ImportadorProvaService importadorProvaService, GabaritoService gabaritoService) {
        this.importadorProvaService = importadorProvaService;
        this.gabaritoService = gabaritoService;
    }

    // Mostra a pagina com o formulario de upload
    @GetMapping("/arquivoPDF")
    public String arquivoPDF() {
        return "importar";
    }

    // Recebe prova e gabarito do formulario, importa os dois e devolve o resultado na mesma tela
    @PostMapping("/arquivoPDF")
    public String enviarArquivo(@RequestParam String curso,
                                @RequestParam int ano,
                                @RequestParam("prova") MultipartFile prova,
                                @RequestParam("gabarito") MultipartFile gabarito,
                                Model model) {

        try {
            // MultipartFile ainda nao existe como arquivo no disco; gravamos cada um num arquivo temporario
            File arquivoProva = File.createTempFile("prova", ".pdf");
            prova.transferTo(arquivoProva);

            File arquivoGabarito = File.createTempFile("gabarito", ".pdf");
            gabarito.transferTo(arquivoGabarito);

            // Importa a prova primeiro (cria Questao e Alternativa, todas com correta=false)
            int questoesImportadas = importadorProvaService.provaMontada(
                    arquivoProva.getAbsolutePath(), null, ano, curso);

            // Depois roda o gabarito por cima: acha a Questao pelo numero e marca a Alternativa certa
            int alternativasCorrigidas = gabaritoService.gabaritoMontado(
                    arquivoGabarito.getAbsolutePath());

            model.addAttribute("sucesso", true);
            model.addAttribute("questoesImportadas", questoesImportadas);
            model.addAttribute("alternativasCorrigidas", alternativasCorrigidas);

        } catch (IOException e) {
            // Se der erro ao ler/gravar os arquivos, avisa na tela em vez de derrubar a aplicacao
            model.addAttribute("sucesso", false);
            model.addAttribute("erro", "Nao foi possivel processar os arquivos enviados.");
        }

        return "importar";
    }
}