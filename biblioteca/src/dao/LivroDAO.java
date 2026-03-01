package dao;

import model.Livro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO livros (id_livro, titulo, autor, disponivel) VALUES (?, ?, ?, 1)";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt   (1, livro.getIdLivro());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar livro: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public Livro buscarPorId(int idLivro) {
        String sql = "SELECT * FROM livros WHERE id_livro = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idLivro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Livro(
                        rs.getInt("id_livro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getBoolean("disponivel")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Livro> listarTodos() {
        return buscarLivros(null);
    }

    public List<Livro> listarDisponiveis() {
        return buscarLivros(true);
    }

    public List<Livro> listarEmprestados() {
        return buscarLivros(false);
    }

    private List<Livro> buscarLivros(Boolean disponivel) {
        String sql = "SELECT * FROM livros"
                + (disponivel != null ? " WHERE disponivel = " + (disponivel ? 1 : 0) : "")
                + " ORDER BY id_livro";
        List<Livro> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                lista.add(new Livro(
                        rs.getInt("id_livro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getBoolean("disponivel")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void atualizarDisponibilidade(int idLivro, boolean disponivel) {
        String sql = "UPDATE livros SET disponivel = ? WHERE id_livro = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, disponivel);
            ps.setInt(2, idLivro);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar livro: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }
    public String excluir(int idLivro) {
        // Verifica se o livro está emprestado antes de excluir
        String sqlVerifica = "SELECT COUNT(*) FROM emprestimos WHERE id_livro = ? AND status = 'ATIVO'";
        String sqlExclui   = "DELETE FROM livros WHERE id_livro = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            // Primeiro verifica se está emprestado
            PreparedStatement psVerifica = conn.prepareStatement(sqlVerifica);
            psVerifica.setInt(1, idLivro);
            ResultSet rs = psVerifica.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "BLOQUEADO"; // livro está emprestado, não pode excluir
            }

            // Se não está, exclui
            PreparedStatement psExclui = conn.prepareStatement(sqlExclui);
            psExclui.setInt(1, idLivro);
            int linhas = psExclui.executeUpdate();
            return linhas > 0 ? "OK" : "NAO_ENCONTRADO";

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir livro: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }
}