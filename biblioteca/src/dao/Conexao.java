package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // ⚙️ ALTERE A SENHA AQUI para a senha do seu MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_escolar?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA   = "1234"; // ← coloque sua senha real aqui!

    public static Connection getConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado! Verifique se adicionou o .jar ao projeto.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao MySQL: " + e.getMessage(), e);
        }
    }

    public static void fechar(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { /* ignora */ }
        }
    }
}