DROP DATABASE IF EXISTS biblioteca_escolar;

CREATE DATABASE biblioteca_escolar;
USE biblioteca_escolar;

CREATE TABLE alunos (
    id_aluno INT PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    turma    VARCHAR(20)  NOT NULL
);

CREATE TABLE livros (
    id_livro   INT PRIMARY KEY,
    titulo     VARCHAR(200) NOT NULL,
    autor      VARCHAR(100) NOT NULL,
    disponivel TINYINT(1) DEFAULT 1
);

CREATE TABLE emprestimos (
    id_emprestimo           INT AUTO_INCREMENT PRIMARY KEY,
    id_aluno                INT NOT NULL,
    id_livro                INT NOT NULL,
    data_emprestimo         DATE NOT NULL,
    data_prevista_devolucao DATE NOT NULL,
    data_devolucao          DATE DEFAULT NULL,
    status                  ENUM('ATIVO','FINALIZADO') DEFAULT 'ATIVO',
    FOREIGN KEY (id_aluno) REFERENCES alunos(id_aluno),
    FOREIGN KEY (id_livro) REFERENCES livros(id_livro)
);