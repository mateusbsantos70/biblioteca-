package model;

public class Aluno {
    // os 3 dados que cada aluno tem
    private int idAluno;
    private String nome;
    private String turma;
    // construtor - usado para criar um aluno novo
    public Aluno( int idAluno, String nome, String turma){
        this.idAluno= idAluno;
        this.nome = nome;
        this.turma =turma;
    }
    //metodos para ler os dados
    public int getIdAluno(){ return idAluno;}
    public String getNome(){ return nome;}
    public String getturma(){ return turma;}

    //como o aluno aparece quando você der println


   @Override
    public String toString(){
        return "[ID:"+idAluno+"]"+nome+"| Turma: " + turma;
   }
}
