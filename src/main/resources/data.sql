INSERT INTO tema (nome, descricao) VALUES ('Banco de Dados', 'Modelagem e SQL');

INSERT INTO questao (enunciado, dificuldade, tema_id) VALUES ('Qual comando SQL é usado para buscar dados de uma tabela?', 'FACIL', 1);

INSERT INTO alternativa (texto, correta, questao_id) VALUES ('SELECT', true, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('INSERT', false, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('UPDATE', false, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('DELETE', false, 1);

INSERT INTO aluno (nome, email, senha, ativo, ra, curso, semestre) VALUES
    ('Ana Beatriz Souza', 'ana.souza@aluno.umc.br', '123456', true, '2023001', 'Sistemas de Informação', 8);

INSERT INTO aluno (nome, email, senha, ativo, ra, curso, semestre) VALUES
    ('João Pedro Lima', 'joao.lima@aluno.umc.br', '123456', true, '2023002', 'Sistemas de Informação', 6);

INSERT INTO professor (nome, email, senha, ativo, registro_profissional, disciplina) VALUES
    ('Carlos Eduardo Mota', 'carlos.mota@umc.br', '123456', true, 'PROF-0456', 'Engenharia de Software');