package model;

import java.time.LocalDate;

public class Emprestimo {

    public static final String ATIVO      = "ATIVO";
    public static final String FINALIZADO = "FINALIZADO";

    private int       idEmprestimo;
    private int       idAluno;
    private int       idLivro;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private String    status;

    // Campos extras para exibição na tela
    private String nomeAluno;
    private String tituloLivro;

    // Construtor para criar um empréstimo novo
    public Emprestimo(int idAluno, int idLivro) {
        this.idAluno               = idAluno;
        this.idLivro               = idLivro;
        this.dataEmprestimo        = LocalDate.now();
        this.dataPrevistaDevolucao = LocalDate.now().plusDays(7);
        this.dataDevolucao         = null;
        this.status                = ATIVO;
    }

    // Construtor para carregar do banco de dados
    public Emprestimo() {}

    public boolean isAtrasado() {
        return ATIVO.equals(status) && LocalDate.now().isAfter(dataPrevistaDevolucao);
    }

    public int       getIdEmprestimo()                      { return idEmprestimo; }
    public void      setIdEmprestimo(int id)                { this.idEmprestimo = id; }
    public int       getIdAluno()                           { return idAluno; }
    public void      setIdAluno(int id)                     { this.idAluno = id; }
    public int       getIdLivro()                           { return idLivro; }
    public void      setIdLivro(int id)                     { this.idLivro = id; }
    public LocalDate getDataEmprestimo()                    { return dataEmprestimo; }
    public void      setDataEmprestimo(LocalDate d)         { this.dataEmprestimo = d; }
    public LocalDate getDataPrevistaDevolucao()             { return dataPrevistaDevolucao; }
    public void      setDataPrevistaDevolucao(LocalDate d)  { this.dataPrevistaDevolucao = d; }
    public LocalDate getDataDevolucao()                     { return dataDevolucao; }
    public void      setDataDevolucao(LocalDate d)          { this.dataDevolucao = d; }
    public String    getStatus()                            { return status; }
    public void      setStatus(String s)                    { this.status = s; }
    public String    getNomeAluno()                         { return nomeAluno; }
    public void      setNomeAluno(String n)                 { this.nomeAluno = n; }
    public String    getTituloLivro()                       { return tituloLivro; }
    public void      setTituloLivro(String t)               { this.tituloLivro = t; }

    @Override
    public String toString() {
        return "[#" + idEmprestimo + "] "
                + (nomeAluno != null ? nomeAluno : "Aluno " + idAluno)
                + " → " + (tituloLivro != null ? tituloLivro : "Livro " + idLivro)
                + " | Prev: " + dataPrevistaDevolucao
                + " | " + status
                + (isAtrasado() ? " ⚠ ATRASADO" : "");
    }
}