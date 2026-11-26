package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// A foto não pode voltar a ser @Lob: no PostgreSQL isso vira "large object", que só é lido dentro de transação,
// e qualquer lista de alunos carregada fora de transação quebrava quando algum aluno tinha foto.
class FotoPerfilMapeamentoTest {

    @Test
    void fotoDePerfilEhGuardadaComoBytesComunsNaColunaNova() throws NoSuchFieldException {
        Field campo = Usuario.class.getDeclaredField("fotoPerfil");

        assertFalse(campo.isAnnotationPresent(Lob.class));
        assertEquals("foto_perfil_bytes", campo.getAnnotation(Column.class).name());
    }
}
