package com.tcc.trilha_do_saber.controller;

import java.util.List;
import com.tcc.trilha_do_saber.model.Alternativa;
import com.tcc.trilha_do_saber.model.Questao;
import com.tcc.trilha_do_saber.model.Resposta;
import com.tcc.trilha_do_saber.repository.AlternativaRepository;
import com.tcc.trilha_do_saber.repository.QuestaoRepository;
import com.tcc.trilha_do_saber.service.RespostaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // Diz ao Spring que essa classe atende requisições HTTP e devolve páginas (não dados brutos)
public class QuestaoController {

    // Dependência: instância do repositório, injetada pelo Spring
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final RespostaService respostaService;

    // Construtor: o Spring entrega automaticamente a implementação do QuestaoRepository aqui
    public QuestaoController(QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository, RespostaService respostaService) {
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.respostaService = respostaService;
    }

    // Atende GET em /questao/1, /questao/2, etc. — {id} é o pedaço variável da URL
    @GetMapping("/questao/{id}")
    public String questao(@PathVariable Long id, Model model){
        // @PathVariable pega o valor da URL; Model é o "carrinho" de dados pra View

        // Busca a Questao no banco pelo id; se não achar, lança erro
        Questao questao = questaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Questão não encontrada"));

        List<Alternativa> alternativas = alternativaRepository.findByQuestaoId(questao.getId());

        // Coloca a questao no carrinho, com o apelido "questao" (usado no HTML depois)
        model.addAttribute("questao", questao);
        model.addAttribute("alternativas", alternativas);

        // Diz ao Spring pra renderizar templates/questao.html
        return "questao";
    }

    // Atende POST em /responder — chamado quando o formulário do questao.html é enviado
    @PostMapping("/responder")
    public String resposta(@RequestParam Long questaoId, @RequestParam Long alternativaId, @RequestParam String nomeUsuario, Model model) {
        // Cada @RequestParam captura um campo do formulário pelo "name"; os nomes precisam bater com o HTML

        // Chama a regra de negócio pronta: busca questão e alternativa, verifica acerto, salva no banco e devolve o resultado
        Resposta resposta = respostaService.responder(questaoId, alternativaId, nomeUsuario);

        // Coloca o resultado no carrinho, com o apelido "resposta" (vai ser usado no resultado.html)
        model.addAttribute("resposta", resposta);

        // Diz ao Spring pra renderizar templates/resultado.html
        return "resultado";
    }

}