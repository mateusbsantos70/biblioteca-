package model;

public class Livro {

    private int     idLivro;
    private String  titulo;
    private String  autor;
    private boolean disponivel;

    // Construtor com 3 parâmetros (disponivel começa true)
    public Livro(int idLivro, String titulo, String autor) {
        this.idLivro    = idLivro;
        this.titulo     = titulo;
        this.autor      = autor;
        this.disponivel = true;
    }

    // Construtor com 4 parâmetros (você passa o disponivel)
    public Livro(int idLivro, String titulo, String autor, boolean disponivel) {
        this.idLivro    = idLivro;
        this.titulo     = titulo;
        this.autor      = autor;
        this.disponivel = disponivel;
    }

    public int     getIdLivro()             { return idLivro; }
    public String  getTitulo()              { return titulo; }
    public String  getAutor()               { return autor; }
    public boolean isDisponivel()           { return disponivel; }
    public void    setDisponivel(boolean d) { this.disponivel = d; }

    public String getDisponivelTexto() {
        return disponivel ? "Disponível" : "Emprestado";
    }

    @Override
    public String toString() {
        return "[ID: " + idLivro + "] " + titulo + " | " + autor + " | " + getDisponivelTexto();
    }
}