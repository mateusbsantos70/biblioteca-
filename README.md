📚 Sistema de Biblioteca Escolar
Um sistema de gerenciamento de biblioteca via linha de comando (CLI) desenvolvido em Java com persistência de dados em MySQL utilizando JDBC. O projeto aplica o padrão de arquitetura MVC/DAO para separar a interface, a lógica de negócios e o acesso ao banco de dados.

✨ Funcionalidades
Gerenciamento de Alunos: Cadastro, listagem e exclusão.

Gerenciamento de Livros: Cadastro, listagem e exclusão.

Controle de Empréstimos e Devoluções: Registra a saída de livros e baixa de devoluções com cálculo de atrasos.

Relatórios: Visualização rápida de livros disponíveis/emprestados e empréstimos ativos/atrasados.

🛡️ Regras de Negócio e Validações:

Um aluno não pode fazer novos empréstimos se tiver livros em atraso.

Limite máximo de 3 empréstimos ativos por aluno.

Prazo padrão de devolução de 7 dias.

Bloqueio de exclusão: Não é possível excluir alunos ou livros que possuam vínculos com empréstimos ativos.

🛠️ Tecnologias Utilizadas
Linguagem: Java (JDK 8 ou superior)

Banco de Dados: MySQL

Conectividade: JDBC (MySQL Connector/J)

Arquitetura: DAO (Data Access Object) e Service Pattern

🚀 Como Executar o Projeto
1. Configurar o Banco de Dados:

Abra o seu SGBD MySQL (ex: MySQL Workbench).

Execute o script contido no arquivo Biblioteca.sql para criar o banco de dados biblioteca_escolar e suas respectivas tabelas.

2. Configurar a Conexão:

No projeto Java, acesse o arquivo dao/Conexao.java.

Altere a constante SENHA (e USUARIO, se necessário) para as credenciais do seu banco de dados local.

Java
private static final String USUARIO = "root";
private static final String SENHA   = "sua_senha_aqui";
3. Adicionar o Driver MySQL:

Certifique-se de adicionar o arquivo .jar do MySQL Connector/J ao Build Path ou às dependências do seu projeto (via Maven/Gradle, se aplicável).

4. Iniciar a Aplicação:

Rode a classe principal que chama MenuConsole.iniciar() para abrir o menu interativo no terminal.
