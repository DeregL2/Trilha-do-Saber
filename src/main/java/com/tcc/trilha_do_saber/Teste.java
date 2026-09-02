package com.tcc.trilha_do_saber;

import com.tcc.trilha_do_saber.service.ImportadorProvaService;

public class Teste {
    public static void main(String[] args) {


        ImportadorProvaService importar = new ImportadorProvaService(null, null);

        importar.provaMontada("src/main/resources/provas/s1_prova.pdf", null);

        System.out.println(importar);
    }
}
