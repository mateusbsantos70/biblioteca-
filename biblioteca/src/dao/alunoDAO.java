package dao;

import model.Aluno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class alunoDAO {

    public boolean inserir(Aluno aluno) {
        String sql = "INSERT INTO alunos (id_aluno, nome, turma) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt   (1, aluno.getIdAluno());
            ps.setString(2, aluno.getNome());
            ps.setString(3, aluno.getturma());
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // ID já existe
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar aluno: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public Aluno buscarPorId(int idAluno) {
        String sql = "SELECT * FROM alunos WHERE id_aluno = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Aluno(rs.getInt("id_aluno"), rs.getString("nome"), rs.getString("turma"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar aluno: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Aluno> listarTodos() {
        String sql = "SELECT * FROM alunos ORDER BY id_aluno";
        List<Aluno> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                lista.add(new Aluno(rs.getInt("id_aluno"), rs.getString("nome"), rs.getString("turma")));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alunos: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }
    public String excluir(int idAluno) {
        // Verifica se aluno tem empréstimos ativos antes de excluir
        String sqlVerifica = "SELECT COUNT(*) FROM emprestimos WHERE id_aluno = ? AND status = 'ATIVO'";
        String sqlExclui   = "DELETE FROM alunos WHERE id_aluno = ?";
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            // Primeiro verifica se tem empréstimo ativo
            PreparedStatement psVerifica = conn.prepareStatement(sqlVerifica);
            psVerifica.setInt(1, idAluno);
            ResultSet rs = psVerifica.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "BLOQUEADO"; // tem empréstimo ativo, não pode excluir
            }

            // Se não tem, exclui
            PreparedStatement psExclui = conn.prepareStatement(sqlExclui);
            psExclui.setInt(1, idAluno);
            int linhas = psExclui.executeUpdate();
            return linhas > 0 ? "OK" : "NAO_ENCONTRADO";

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir aluno: " + e.getMessage(), e);
        } finally {
            Conexao.fechar(conn);
        }
    }

}