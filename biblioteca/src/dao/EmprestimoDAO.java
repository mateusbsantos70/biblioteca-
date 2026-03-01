package dao;

import model.Emprestimo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public void inserir(Emprestimo e) {
        String sql = "INSERT INTO emprestimos (id_aluno, id_livro, data_emprestimo, data_prevista_devolucao, status) VALUES (?, ?, ?, ?, 'ATIVO')";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, e.getIdAluno());
            ps.setInt   (2, e.getIdLivro());
            ps.setDate  (3, Date.valueOf(e.getDataEmprestimo()));
            ps.setDate  (4, Date.valueOf(e.getDataPrevistaDevolucao()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) e.setIdEmprestimo(keys.getInt(1));
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao registrar empréstimo: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public Emprestimo buscarPorId(int idEmprestimo) {
        String sql = "SELECT e.*, a.nome, l.titulo FROM emprestimos e "
                + "JOIN alunos a ON e.id_aluno = a.id_aluno "
                + "JOIN livros l ON e.id_livro = l.id_livro "
                + "WHERE e.id_emprestimo = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEmprestimo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar empréstimo: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Emprestimo> listarAtivos() {
        return listar("WHERE e.status = 'ATIVO'");
    }

    public List<Emprestimo> listarAtrasados() {
        return listar("WHERE e.status = 'ATIVO' AND e.data_prevista_devolucao < CURDATE()");
    }

    public List<Emprestimo> listarPorAluno(int idAluno) {
        return listar("WHERE e.id_aluno = " + idAluno + " AND e.status = 'ATIVO'");
    }

    public int contarAtivosDoAluno(int idAluno) {
        String sql = "SELECT COUNT(*) FROM emprestimos WHERE id_aluno = ? AND status = 'ATIVO'";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao contar empréstimos: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public boolean alunoTemAtraso(int idAluno) {
        String sql = "SELECT COUNT(*) FROM emprestimos WHERE id_aluno = ? AND status = 'ATIVO' AND data_prevista_devolucao < CURDATE()";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao verificar atraso: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void finalizar(int idEmprestimo) {
        String sql = "UPDATE emprestimos SET status = 'FINALIZADO', data_devolucao = CURDATE() WHERE id_emprestimo = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEmprestimo);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao finalizar empréstimo: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    private List<Emprestimo> listar(String filtro) {
        String sql = "SELECT e.*, a.nome, l.titulo FROM emprestimos e "
                + "JOIN alunos a ON e.id_aluno = a.id_aluno "
                + "JOIN livros l ON e.id_livro = l.id_livro "
                + filtro + " ORDER BY e.id_emprestimo";
        List<Emprestimo> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar empréstimos: " + ex.getMessage(), ex);
        } finally {
            Conexao.fechar(conn);
        }
    }

    private Emprestimo mapear(ResultSet rs) throws SQLException {
        Emprestimo e = new Emprestimo();
        e.setIdEmprestimo(rs.getInt("id_emprestimo"));
        e.setIdAluno     (rs.getInt("id_aluno"));
        e.setIdLivro     (rs.getInt("id_livro"));
        e.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        e.setDataPrevistaDevolucao(rs.getDate("data_prevista_devolucao").toLocalDate());
        Date dev = rs.getDate("data_devolucao");
        if (dev != null) e.setDataDevolucao(dev.toLocalDate());
        e.setStatus     (rs.getString("status"));
        e.setNomeAluno  (rs.getString("nome"));
        e.setTituloLivro(rs.getString("titulo"));
        return e;
    }
}