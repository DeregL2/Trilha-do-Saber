INSERT INTO tema (nome, descricao) VALUES ('Banco de Dados', 'Modelagem e SQL');

INSERT INTO questao (enunciado, dificuldade, tema_id) VALUES ('Qual comando SQL é usado para buscar dados de uma tabela?', 'FACIL', 1);

INSERT INTO alternativa (texto, correta, questao_id) VALUES ('SELECT', true, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('INSERT', false, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('UPDATE', false, 1);
INSERT INTO alternativa (texto, correta, questao_id) VALUES ('DELETE', false, 1);