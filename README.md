# 📚 Trilha do Saber

Plataforma de estudos adaptativa para preparação ao **ENADE**, desenvolvida como Trabalho de Conclusão de Curso (TCC) do curso de **Sistemas de Informação** da UMC.

> Organiza o banco de questões de provas anteriores, aplica exercícios ao aluno e acompanha seu desempenho, ajudando a direcionar os estudos para os temas com maior necessidade de reforço.

---

## 🎯 Sobre o projeto

O ENADE é uma prova essencial para a avaliação dos cursos de graduação no Brasil, mas encontrar um banco de questões organizado e confiável para treino é um desafio — provas de edições anteriores costumam estar disponíveis apenas em PDFs soltos, sem nenhuma estrutura de estudo por trás.

O **Trilha do Saber** resolve isso automatizando a importação de provas oficiais do ENADE direto do PDF original, organizando as questões por curso/ano e permitindo que o aluno resolva simulados enquanto o sistema acompanha e confere seu desempenho, criando uma trilha de estudo personalizada — com visão separada para aluno, professor, coordenador e administração.

## ✨ Funcionalidades

**Importação automática de provas (núcleo do projeto)**
- Extração automática de enunciados, alternativas e imagens/diagramas a partir do PDF oficial do ENADE (layout em duas colunas), com reconhecimento de correção de ligaduras tipográficas, recomposição de parágrafos e remoção de elementos irrelevantes (cabeçalhos repetidos, códigos de barra, links de referência bibliográfica)
- Importação do gabarito oficial em PDF, com múltiplas estratégias de leitura para diferentes formatos de gabarito
- Cadastro manual de questões no banco, como alternativa à importação automática

**Área do aluno**
- Cadastro com solicitação de acesso e aceite dos termos de uso/LGPD
- Verificação em duas etapas por e-mail e fluxo de primeiro acesso
- Resolução de simulados e provas aplicadas pela turma, com correção automática
- Dashboard de desempenho e ranking da turma

**Área do professor**
- Painel com as turmas alocadas e acompanhamento dos alunos
- Dashboard de desempenho por turma

**Área do coordenador**
- Gestão do banco de questões, turmas e alocação de disciplinas/professores
- Importação em massa de alunos e professores via planilha
- Aplicação de provas para turmas inteiras
- Dashboards central, por curso e por turma

**Administração**
- Gestão de todos os tipos de conta (aluno, professor, coordenador, admin)
- Log de auditoria de ações administrativas, com expurgo automático de registros antigos

**Segurança e qualidade**
- Senhas com hash BCrypt e verificação de e-mail único entre todos os tipos de conta (impede o mesmo e-mail cadastrado como aluno e professor, por exemplo)
- Redefinição de senha por e-mail com token de uso único
- Encerramento automático de simulados expirados (tarefa agendada)
- Suíte de testes unitários e de integração (JUnit + Mockito) com cobertura acompanhada via JaCoCo

## 🛠️ Tecnologias utilizadas

**Back-end**
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Apache PDFBox](https://img.shields.io/badge/Apache%20PDFBox-3.0.3-red?logo=apache&logoColor=white)

**Front-end**
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-FF6384?logo=chartdotjs&logoColor=white)
![Anime.js](https://img.shields.io/badge/Anime.js-FF6B6B)

**Banco de Dados**
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)

**Testes & Qualidade**
![JUnit](https://img.shields.io/badge/JUnit-25A162?logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78C257)

**Outros**
- Spring Data JPA + Hibernate
- Spring Security + BCrypt
- Envio de e-mail transacional via Resend (verificação em duas etapas, redefinição de senha)
- Deploy via Render

---

## ✅ Testes

O projeto tem uma suíte com centenas de testes unitários e de integração (JUnit 5 + Mockito), cobrindo controllers, services e regras de negócio, com cobertura acompanhada via JaCoCo.

---

## 👥 Integrantes

| Nome |
|---|
| **Derick Alves Maschio** |
| **Isabella Nicoliche Silva** |
| **Thaíssa Victoria Bianchini de Oliveira** |

**Curso:** Sistemas de Informação — 8ª B Noturno
**Instituição:** UMC (Universidade de Mogi das Cruzes)
**Orientador:** Prof. Leonardo Cavalcante Alvino

---

<p align="center">Feito com 💛 por alunos de Sistemas de Informação — UMC</p>