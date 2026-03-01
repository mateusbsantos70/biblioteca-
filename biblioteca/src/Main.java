import dao.Conexao;
import ui.MenuConsole;
import ui.servidorweb;

public class Main {
    public static void main(String[] args) throws Exception {

        // Testa a conexão com o banco antes de começar
        System.out.println("Conectando ao banco de dados...");
        Conexao.getConexao(); // vai lançar erro aqui se a senha estiver errada
        System.out.println("✔ Banco de dados conectado!");

        // Inicia o servidor web (acesse http://localhost:8080)
        servidorweb.iniciar();

        // Inicia o menu no console (terminal)
        MenuConsole.iniciar();
    }
}
