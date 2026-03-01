package ui;
import  service.BibliotecaService;
import model.*;
import java.util.List;
import java.util.Scanner;

public class MenuConsole {
    private static final BibliotecaService servico = new BibliotecaService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void iniciar() {
        System.out.println("===========================================");
        System.out.println("   SISTEMA DE BIBLIOTECA ESCOLAR");
        System.out.println("===========================================");
        int opcao;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Cadastrar Aluno");
            System.out.println("2. Cadastrar Livro");
            System.out.println("3. Listar Alunos");
            System.out.println("4. Listar Livros");
            System.out.println("5. Realizar Empréstimo");
            System.out.println("6. Realizar Devolução");
            System.out.println("7. Excluir Aluno");
            System.out.println("8. Excluir Livro");
            System.out.println("9. Relatórios");
            System.out.println("10. Sair");
            System.out.print("Escolha: ");
            opcao = lerInt();
            switch (opcao) {
                case 1 -> cadastrarAluno();
                case 2 -> cadastrarLivro();
                case 3 -> listarAlunos();
                case 4 -> listarLivros();
                case 5 -> realizarEmprestimo();
                case 6 -> realizarDevolucao();
                case 7  -> excluirAluno();
                case 8  -> excluirLivro();
                case 9  -> menuRelatorios();
                case 10 -> System.out.println("Encerrando... Até logo!");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 10);
    }


private static void cadastrarAluno(){
    System.out.println("\n-- Cadastrar Aluno --");
    System.out.print("ID do aluno: ");    int id = lerInt();
    System.out.print("Nome: ");           String nome = scanner.nextLine();
    System.out.print("Turma (ex: 3A): "); String turma = scanner.nextLine();
    System.out.println(servico.cadastrarAluno(id, nome, turma));
}
private static void cadastrarLivro() {
    System.out.println("\n-- Cadastrar Livro --");
    System.out.print("ID do livro: ");  int id = lerInt();
    System.out.print("Título: ");       String titulo = scanner.nextLine();
    System.out.print("Autor: ");        String autor = scanner.nextLine();
    System.out.println(servico.cadastrarLivro(id, titulo, autor));
}
    private static void listarAlunos() {
        System.out.println("\n-- Lista de Alunos --");
        List<Aluno> lista = servico.listarAlunos();
        if (lista.isEmpty()) { System.out.println("Nenhum aluno cadastrado."); return; }
        lista.forEach(System.out::println);
    }

    private static void listarLivros() {
        System.out.println("\n-- Lista de Livros --");
        List<Livro> lista = servico.listarLivros();
        if (lista.isEmpty()) { System.out.println("Nenhum livro cadastrado."); return; }
        lista.forEach(System.out::println);
    }

    private static void realizarEmprestimo() {
        System.out.println("\n-- Realizar Empréstimo --");
        System.out.print("ID do aluno: "); int idAluno = lerInt();
        System.out.print("ID do livro: "); int idLivro = lerInt();
        System.out.println(servico.realizarEmprestimo(idAluno, idLivro));
    }

    private static void realizarDevolucao() {
        System.out.println("\n-- Realizar Devolução --");
        System.out.print("ID do empréstimo: "); int id = lerInt();
        System.out.println(servico.realizarDevolucao(id));
    }

    private static void menuRelatorios() {
        System.out.println("\n-- Relatórios --");
        System.out.println("1. Livros disponíveis");
        System.out.println("2. Livros emprestados");
        System.out.println("3. Empréstimos ativos");
        System.out.println("4. Empréstimos atrasados");
        System.out.print("Escolha: ");
        switch (lerInt()) {
            case 1 -> servico.listarDisponiveis().forEach(System.out::println);
            case 2 -> servico.listarEmprestados().forEach(System.out::println);
            case 3 -> servico.relatorioAtivos().forEach(System.out::println);
            case 4 -> servico.relatorioAtrasados().forEach(System.out::println);
            default -> System.out.println("Opção inválida.");
        }
    }
    private static void excluirAluno() {
        System.out.println("\n-- Excluir Aluno --");
        System.out.print("ID do aluno a excluir: ");
        int id = lerInt();
        System.out.println(servico.excluirAluno(id));
    }

    private static void excluirLivro() {
        System.out.println("\n-- Excluir Livro --");
        System.out.print("ID do livro a excluir: ");
        int id = lerInt();
        System.out.println(servico.excluirLivro(id));
    }

    private static int lerInt() {
        try {
            int v = Integer.parseInt(scanner.nextLine().trim());
            return v;
        } catch (NumberFormatException e) {
            System.out.print("Digite um número válido: ");
            return lerInt();
        }
    }

}

