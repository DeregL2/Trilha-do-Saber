package com.tcc.trilha_do_saber.dto;

import java.util.List;

public class TurmaComAlunosDTO {

    private final TurmaAlvoDTO turma;
    private final List<AlunoOpcaoDTO> alunos;

    public TurmaComAlunosDTO(TurmaAlvoDTO turma, List<AlunoOpcaoDTO> alunos) {
        this.turma = turma;
        this.alunos = List.copyOf(alunos);
    }

    public TurmaAlvoDTO getTurma() { return turma; }
    public List<AlunoOpcaoDTO> getAlunos() { return alunos; }

    public int getTotalAlunos() {
        return alunos.size();
    }
}