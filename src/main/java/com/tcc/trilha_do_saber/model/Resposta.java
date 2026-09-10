package com.tcc.trilha_do_saber.model;

import java.time.LocalDateTime;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
// Impede duas respostas para a mesma questao dentro do mesmo simulado
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"simulado_id", "questao_id"}))
public class Resposta {

    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    private String nomeUsuario;

    @ManyToOne
    @JoinColumn(name = "questao_id")
    private Questao questao;

    @ManyToOne
    @JoinColumn(name = "alternativa_id")
    private Alternativa alternativaEscolhida;

    @ManyToOne
    @JoinColumn(name = "simulado_id") // Null quando a questao foi respondida avulsa
    private Simulado simulado;

    private boolean acertou;

    private LocalDateTime dataHora;

    // Construtor JPA
    public Resposta() {

    }

    // Construtor da Classe (questao avulsa, sem simulado)
    public Resposta(String nomeUsuario, Questao questao, Alternativa alternativaEscolhida,
                    boolean acertou, LocalDateTime dataHora) {

        this(nomeUsuario, questao, alternativaEscolhida, acertou, dataHora, null);
    }

    // Construtor da Classe (dentro de um simulado)
    public Resposta(String nomeUsuario, Questao questao, Alternativa alternativaEscolhida,
                    boolean acertou, LocalDateTime dataHora, Simulado simulado) {

        this.nomeUsuario = nomeUsuario;
        this.questao = questao;
        this.alternativaEscolhida = alternativaEscolhida;
        this.acertou = acertou;
        this.dataHora = dataHora;
        this.simulado = simulado;
    }

    // Usado quando o aluno volta e troca a alternativa
    public void alterarResposta(Alternativa novaAlternativa, boolean acertou, LocalDateTime dataHora) {
        this.alternativaEscolhida = novaAlternativa;
        this.acertou = acertou;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Questao getQuestao() {
        return questao;
    }

    public Alternativa getAlternativaEscolhida() {
        return alternativaEscolhida;
    }

    public Simulado getSimulado() {
        return simulado;
    }

    public boolean isAcertou() {
        return acertou;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}