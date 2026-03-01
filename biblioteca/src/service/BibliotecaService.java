package service;

import dao.alunoDAO;
import dao.LivroDAO;
import dao.EmprestimoDAO;
import model.Aluno;
import model.Livro;
import model.Emprestimo;
import java.util.List;

public class BibliotecaService {

    private final alunoDAO      alunoDAO      = new alunoDAO();
    private final LivroDAO      livroDAO      = new LivroDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    // ── ALUNOS ──────────────────────────────────────────────

    public String cadastrarAluno(int id, String nome, String turma) {
        if (alunoDAO.buscarPorId(id) != null)
            return "❌ Já existe um aluno com ID " + id;
        alunoDAO.inserir(new Aluno(id, nome, turma));
        return "✔ Aluno cadastrado com sucesso!";
    }

    public List<Aluno> listarAlunos() {
        return alunoDAO.listarTodos();
    }

    // ── LIVROS ───────────────────────────────────────────────

    public String cadastrarLivro(int id, String titulo, String autor) {
        if (livroDAO.buscarPorId(id) != null)
            return "❌ Já existe um livro com ID " + id;
        livroDAO.inserir(new Livro(id, titulo, autor));
        return "✔ Livro cadastrado com sucesso!";
    }

    public List<Livro> listarLivros()      { return livroDAO.listarTodos(); }
    public List<Livro> listarDisponiveis() { return livroDAO.listarDisponiveis(); }
    public List<Livro> listarEmprestados() { return livroDAO.listarEmprestados(); }

    // ── EMPRÉSTIMOS ──────────────────────────────────────────

    public String realizarEmprestimo(int idAluno, int idLivro) {
        if (alunoDAO.buscarPorId(idAluno) == null)
            return "❌ Aluno não encontrado.";

        if (emprestimoDAO.alunoTemAtraso(idAluno))
            return "❌ Aluno possui empréstimo em ATRASO. Devolva antes de pegar novo livro.";

        if (emprestimoDAO.contarAtivosDoAluno(idAluno) >= 3)
            return "❌ Aluno já possui 3 empréstimos ativos.";

        Livro livro = livroDAO.buscarPorId(idLivro);
        if (livro == null)         return "❌ Livro não encontrado.";
        if (!livro.isDisponivel()) return "❌ Livro não está disponível.";

        emprestimoDAO.inserir(new Emprestimo(idAluno, idLivro));
        livroDAO.atualizarDisponibilidade(idLivro, false);
        return "✔ Empréstimo realizado! Devolução prevista em 7 dias.";
    }

    public String realizarDevolucao(int idEmprestimo) {
        Emprestimo emp = emprestimoDAO.buscarPorId(idEmprestimo);
        if (emp == null)
            return "❌ Empréstimo não encontrado.";
        if (emp.getStatus().equals("FINALIZADO"))
            return "❌ Este empréstimo já foi finalizado.";

        emprestimoDAO.finalizar(idEmprestimo);
        livroDAO.atualizarDisponibilidade(emp.getIdLivro(), true);

        String aviso = emp.isAtrasado() ? " (⚠ Devolvido com ATRASO)" : "";
        return "✔ Devolução registrada com sucesso!" + aviso;
    }

    // ── RELATÓRIOS ───────────────────────────────────────────

    public List<Emprestimo> relatorioAtivos()    { return emprestimoDAO.listarAtivos(); }
    public List<Emprestimo> relatorioAtrasados() { return emprestimoDAO.listarAtrasados(); }

    public String excluirAluno(int id) {
        if (alunoDAO.buscarPorId(id) == null)
            return "❌ Aluno não encontrado.";

        String resultado = alunoDAO.excluir(id);
        return switch (resultado) {
            case "OK"           -> "✔ Aluno excluído com sucesso!";
            case "BLOQUEADO"    -> "❌ Aluno possui empréstimos ativos e não pode ser excluído.";
            case "NAO_ENCONTRADO" -> "❌ Aluno não encontrado.";
            default             -> "❌ Erro desconhecido.";
        };
    }

    public String excluirLivro(int id) {
        if (livroDAO.buscarPorId(id) == null)
            return "❌ Livro não encontrado.";

        String resultado = livroDAO.excluir(id);
        return switch (resultado) {
            case "OK"           -> "✔ Livro excluído com sucesso!";
            case "BLOQUEADO"    -> "❌ Livro está emprestado e não pode ser excluído.";
            case "NAO_ENCONTRADO" -> "❌ Livro não encontrado.";
            default             -> "❌ Erro desconhecido.";
        };
    }
}