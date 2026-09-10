package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Fonte {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
     private Long id;

     private String texto;

     // Construtor JPA
    public Fonte() {}

    // Construtor
    public Fonte(String texto) {
        this.texto = texto;
    }

    public Long getId() {return id;}

    public String getTexto() {return texto;}

}
