package com.tcc.trilha_do_saber.controller;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.exception.ProvaDuplicadaException;
import com.tcc.trilha_do_saber.service.GabaritoService;
import com.tcc.trilha_do_saber.service.ImportadorProvaServiceV2;
import jakarta.servlet.http.HttpSession;
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

    private final ImportadorProvaServiceV2 importadorProvaService;
    private final GabaritoService gabaritoService;

    // Construtor
    public ImportacaoController(ImportadorProvaServiceV2 importadorProvaService, GabaritoService gabaritoService) {
        this.importadorProvaService = importadorProvaService;
        this.gabaritoService = gabaritoService;
    }

    // Mostra a pagina com o formulario de upload - so Coordenador e ADM
    @GetMapping("/arquivoPDF")
    public String arquivoPDF(HttpSession session) {
        if (usuarioAutorizado(session) == null) {
            return "redirect:/login";
        }
        return "importar";
    }

    // Recebe prova e gabarito do formulario, importa os dois e devolve o resultado na mesma tela
    @PostMapping("/arquivoPDF")
    public String enviarArquivo(@RequestParam String curso,
                                @RequestParam int ano,
                                @RequestParam("prova") MultipartFile prova,
                                @RequestParam("gabarito") MultipartFile gabarito,
                                Model model,
                                HttpSession session) {

        UsuarioSessaoDTO usuario = usuarioAutorizado(session);
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // MultipartFile ainda nao existe como arquivo no disco; gravamos cada um num arquivo temporario
            File arquivoProva = File.createTempFile("prova", ".pdf");
            prova.transferTo(arquivoProva);

            File arquivoGabarito = File.createTempFile("gabarito", ".pdf");
            gabarito.transferTo(arquivoGabarito);

            // Importa a prova primeiro (cria Questao e Alternativa, todas com correta=false), guardando quem importou
            int questoesImportadas = importadorProvaService.provaMontada(
                    arquivoProva.getAbsolutePath(), ano, curso, usuario.getId(), usuario.getNome());

            // Depois roda o gabarito por cima: acha a Questao pelo numero e marca a Alternativa certa
            int alternativasCorrigidas = gabaritoService.gabaritoMontado(
                    arquivoGabarito.getAbsolutePath());

            model.addAttribute("sucesso", true);
            model.addAttribute("questoesImportadas", questoesImportadas);
            model.addAttribute("alternativasCorrigidas", alternativasCorrigidas);

        } catch (ProvaDuplicadaException e) {
            // Mesmo PDF ja foi importado antes (detectado pelo hash do arquivo); nao importa de novo pra nao duplicar questoes
            model.addAttribute("sucesso", false);
            model.addAttribute("erro", "Essa prova ja foi importada antes (curso " + e.getCurso()
                    + ", ano " + e.getAno() + "). Nao foi importada novamente para evitar duplicidade.");
        } catch (IOException e) {
            // Se der erro ao ler/gravar os arquivos, avisa na tela em vez de derrubar a aplicacao
            model.addAttribute("sucesso", false);
            model.addAttribute("erro", "Nao foi possivel processar os arquivos enviados.");
        }

        return "importar";
    }

    private UsuarioSessaoDTO usuarioAutorizado(HttpSession session) {
        Object logado = session.getAttribute("usuarioLogado");
        if (logado instanceof UsuarioSessaoDTO usuario
                && ("ADMIN".equals(usuario.getTipo()) || "COORDENADOR".equals(usuario.getTipo()))) {
            return usuario;
        }
        return null;
    }
}