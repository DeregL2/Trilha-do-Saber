package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Representa uma tabela no banco de dados
public class Simulado {

    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    private String nomeUsuario;

    private String titulo; // Nome que aparece no topo da tela

    private int duracaoMinutos; // Tempo total do simulado

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim; // Fica null enquanto nao entregou
    private boolean finalizado;

    // Construtor JPA
    public Simulado() {

    }

    // Construtor da Classe
    public Simulado(String nomeUsuario, String titulo, int duracaoMinutos, LocalDateTime dataInicio) {
        this.nomeUsuario = nomeUsuario;
        this.titulo = titulo;
        this.duracaoMinutos = duracaoMinutos;
        this.dataInicio = dataInicio;
        this.finalizado = false; // Todo simulado comeca em andamento
    }

    // Encerra o simulado: grava a data e marca como finalizado
    public void finalizar(LocalDateTime dataFim) {
        this.dataFim = dataFim;
        this.finalizado = true;
    }

    // Calcula a hora limite. Nao vira coluna porque o Hibernate mapeia so os atributos.
    public LocalDateTime getDataLimite() {
        return dataInicio.plusMinutes(duracaoMinutos);
    }

    // Verifica se o tempo ja acabou
    public boolean expirou(LocalDateTime agora) {
        return agora.isAfter(getDataLimite());
    }

    // So permite responder se nao finalizou e nao estourou o tempo
    public boolean estaEmAndamento(LocalDateTime agora) {
        return !finalizado && !expirou(agora);
    }

    public Long getId() {
        return id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public boolean isFinalizado() {
        return finalizado;
    }
}