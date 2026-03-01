package ui;

import com.sun.net.httpserver.*;
import service.BibliotecaService;
import model.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class servidorweb {

    private static final BibliotecaService servico = new BibliotecaService();

    public static void iniciar() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/",          h -> responder(h, paginaInicial()));
        server.createContext("/alunos",    h -> responder(h, paginaAlunos()));
        server.createContext("/livros",    h -> responder(h, paginaLivros()));
        server.createContext("/relatorio", h -> responder(h, paginaRelatorio()));

        server.createContext("/cadastrarAluno", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.cadastrarAluno(Integer.parseInt(p.get("id")), p.get("nome"), p.get("turma"));
            responder(h, paginaMensagem(msg, "/alunos"));
        });

        server.createContext("/cadastrarLivro", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.cadastrarLivro(Integer.parseInt(p.get("id")), p.get("titulo"), p.get("autor"));
            responder(h, paginaMensagem(msg, "/livros"));
        });

        server.createContext("/emprestar", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.realizarEmprestimo(Integer.parseInt(p.get("idAluno")), Integer.parseInt(p.get("idLivro")));
            responder(h, paginaMensagem(msg, "/livros"));
        });

        server.createContext("/devolver", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.realizarDevolucao(Integer.parseInt(p.get("idEmprestimo")));
            responder(h, paginaMensagem(msg, "/relatorio"));
        });

        server.createContext("/excluirAluno", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.excluirAluno(Integer.parseInt(p.get("id")));
            responder(h, paginaMensagem(msg, "/alunos"));
        });

        server.createContext("/excluirLivro", h -> {
            Map<String,String> p = lerParams(lerBody(h));
            String msg = servico.excluirLivro(Integer.parseInt(p.get("id")));
            responder(h, paginaMensagem(msg, "/livros"));
        });

        // Endpoints para o n8n consultar
        server.createContext("/api/atrasados", h -> {
            StringBuilder json = new StringBuilder("[");
            List<Emprestimo> lista = servico.relatorioAtrasados();
            for (int i = 0; i < lista.size(); i++) {
                Emprestimo e = lista.get(i);
                json.append("{")
                        .append("\"id\":").append(e.getIdEmprestimo()).append(",")
                        .append("\"aluno\":\"").append(e.getNomeAluno()).append("\",")
                        .append("\"livro\":\"").append(e.getTituloLivro()).append("\",")
                        .append("\"previsao\":\"").append(e.getDataPrevistaDevolucao()).append("\"")
                        .append("}");
                if (i < lista.size() - 1) json.append(",");
            }
            json.append("]");
            responderJson(h, json.toString());
        });

        server.createContext("/api/ativos", h -> {
            StringBuilder json = new StringBuilder("[");
            List<Emprestimo> lista = servico.relatorioAtivos();
            for (int i = 0; i < lista.size(); i++) {
                Emprestimo e = lista.get(i);
                json.append("{")
                        .append("\"id\":").append(e.getIdEmprestimo()).append(",")
                        .append("\"aluno\":\"").append(e.getNomeAluno()).append("\",")
                        .append("\"livro\":\"").append(e.getTituloLivro()).append("\",")
                        .append("\"previsao\":\"").append(e.getDataPrevistaDevolucao()).append("\",")
                        .append("\"atrasado\":").append(e.isAtrasado())
                        .append("}");
                if (i < lista.size() - 1) json.append(",");
            }
            json.append("]");
            responderJson(h, json.toString());
        });

        server.createContext("/api/resumo", h -> {
            int ativos    = servico.relatorioAtivos().size();
            int atrasados = servico.relatorioAtrasados().size();
            int disponiveis = servico.listarDisponiveis().size();
            int emprestados = servico.listarEmprestados().size();
            String json = "{"
                    + "\"emprestimosAtivos\":"   + ativos    + ","
                    + "\"emprestimosAtrasados\":" + atrasados + ","
                    + "\"livrosDisponiveis\":"   + disponiveis + ","
                    + "\"livrosEmprestados\":"   + emprestados
                    + "}";
            responderJson(h, json);
        });

        server.start();
        System.out.println("✔ Servidor web iniciado! Acesse: http://localhost:8080");
    }

    // ── PÁGINAS HTML ─────────────────────────────────────────

    private static String paginaInicial() {
        return html("Biblioteca Escolar", """
            <div class='hero'>
                <h1>📚 Biblioteca Escolar</h1>
                <p>Sistema de Controle de Empréstimos</p>
            </div>
            <div class='cards'>
                <a href='/alunos'    class='card'>👤 Alunos</a>
                <a href='/livros'    class='card'>📖 Livros</a>
                <a href='/relatorio' class='card'>📊 Relatórios</a>
            </div>
        """);
    }

    private static String paginaAlunos() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>👤 Alunos Cadastrados</h2><table><tr><th>ID</th><th>Nome</th><th>Turma</th><th>Ação</th></tr>");
        for (Aluno a : servico.listarAlunos()) {
            sb.append("<tr><td>").append(a.getIdAluno()).append("</td><td>")
                    .append(a.getNome()).append("</td><td>").append(a.getturma())
                    .append("</td><td>")
                    .append("<form method='post' action='/excluirAluno'>")
                    .append("<input type='hidden' name='id' value='").append(a.getIdAluno()).append("'>")
                    .append("<button type='submit' onclick='return confirm(\"Tem certeza?\")' class='btn-excluir'>🗑 Excluir</button>")
                    .append("</form></td></tr>");
        }
        sb.append("</table>");
        sb.append("""
            <h2>➕ Cadastrar Aluno</h2>
            <form method='post' action='/cadastrarAluno'>
                <input name='id'    placeholder='ID' type='number' required>
                <input name='nome'  placeholder='Nome completo' required>
                <input name='turma' placeholder='Turma (ex: 3A)' required>
                <button type='submit'>Cadastrar</button>
            </form>
        """);
        return html("Alunos", sb.toString());
    }

    private static String paginaLivros() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>📖 Livros do Acervo</h2><table><tr><th>ID</th><th>Título</th><th>Autor</th><th>Status</th><th>Ação</th></tr>");
        for (Livro l : servico.listarLivros()) {
            String status = l.isDisponivel()
                    ? "<span class='disp'>✔ Disponível</span>"
                    : "<span class='empr'>✘ Emprestado</span>";
            sb.append("<tr><td>").append(l.getIdLivro()).append("</td><td>")
                    .append(l.getTitulo()).append("</td><td>").append(l.getAutor())
                    .append("</td><td>").append(status)
                    .append("</td><td>")
                    .append("<form method='post' action='/excluirLivro'>")
                    .append("<input type='hidden' name='id' value='").append(l.getIdLivro()).append("'>")
                    .append("<button type='submit' onclick='return confirm(\"Tem certeza?\")' class='btn-excluir'>🗑 Excluir</button>")
                    .append("</form></td></tr>");
        }
        sb.append("</table>");
        sb.append("""
            <div class='forms'>
            <div>
            <h2>➕ Cadastrar Livro</h2>
            <form method='post' action='/cadastrarLivro'>
                <input name='id'     placeholder='ID' type='number' required>
                <input name='titulo' placeholder='Título' required>
                <input name='autor'  placeholder='Autor' required>
                <button type='submit'>Cadastrar</button>
            </form>
            </div>
            <div>
            <h2>📤 Realizar Empréstimo</h2>
            <form method='post' action='/emprestar'>
                <input name='idAluno' placeholder='ID do Aluno' type='number' required>
                <input name='idLivro' placeholder='ID do Livro' type='number' required>
                <button type='submit'>Emprestar</button>
            </form>
            </div>
            </div>
        """);
        return html("Livros", sb.toString());
    }

    private static String paginaRelatorio() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h2>📊 Empréstimos Ativos</h2><table><tr><th>#</th><th>Aluno</th><th>Livro</th><th>Previsão</th><th>Ação</th></tr>");
        for (Emprestimo e : servico.relatorioAtivos()) {
            String atraso = e.isAtrasado() ? " <span class='atras'>⚠ ATRASADO</span>" : "";
            sb.append("<tr><td>").append(e.getIdEmprestimo())
                    .append("</td><td>").append(e.getNomeAluno())
                    .append("</td><td>").append(e.getTituloLivro())
                    .append("</td><td>").append(e.getDataPrevistaDevolucao()).append(atraso)
                    .append("</td><td>")
                    .append("<form method='post' action='/devolver'>")
                    .append("<input type='hidden' name='idEmprestimo' value='").append(e.getIdEmprestimo()).append("'>")
                    .append("<button type='submit'>Devolver</button></form>")
                    .append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h2>⚠ Empréstimos Atrasados</h2><table><tr><th>#</th><th>Aluno</th><th>Livro</th><th>Previsão</th></tr>");
        for (Emprestimo e : servico.relatorioAtrasados()) {
            sb.append("<tr class='atrasado'><td>").append(e.getIdEmprestimo())
                    .append("</td><td>").append(e.getNomeAluno())
                    .append("</td><td>").append(e.getTituloLivro())
                    .append("</td><td>").append(e.getDataPrevistaDevolucao()).append("</td></tr>");
        }
        sb.append("</table>");
        return html("Relatórios", sb.toString());
    }

    private static String paginaMensagem(String msg, String voltar) {
        boolean ok = msg.startsWith("✔");
        return html("Resultado", "<div class='msg " + (ok ? "ok" : "erro") + "'>" + msg + "</div>"
                + "<a href='" + voltar + "' class='btn'>← Voltar</a>");
    }

    // ── CSS e TEMPLATE ───────────────────────────────────────

    private static String html(String titulo, String conteudo) {
        return """
            <!DOCTYPE html>
            <html lang='pt-BR'>
            <head>
                <meta charset='UTF-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1'>
                <title>%s | Biblioteca</title>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: 'Segoe UI', sans-serif; background: #f0f4f8; color: #333; }
                    nav { background: #1a3c5e; padding: 14px 30px; display: flex; gap: 20px; align-items: center; }
                    nav a { color: #fff; text-decoration: none; font-weight: 500; }
                    nav a:hover { text-decoration: underline; }
                    nav span { color: #7eb8f7; font-weight: bold; font-size: 1.1em; margin-right: auto; }
                    .container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }
                    .hero { text-align: center; padding: 50px 20px; }
                    .hero h1 { font-size: 2.5em; color: #1a3c5e; }
                    .hero p  { color: #666; margin-top: 10px; font-size: 1.1em; }
                    .cards { display: flex; gap: 20px; justify-content: center; flex-wrap: wrap; margin-top: 30px; }
                    .card { background: #1a3c5e; color: white; padding: 30px 40px; border-radius: 12px;
                            text-decoration: none; font-size: 1.2em; font-weight: bold;
                            transition: transform .2s, background .2s; }
                    .card:hover { background: #2a5c8e; transform: translateY(-3px); }
                    h2 { color: #1a3c5e; margin: 30px 0 15px; border-bottom: 2px solid #1a3c5e; padding-bottom: 8px; }
                    table { width: 100%%; border-collapse: collapse; background: white; border-radius: 8px;
                            overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.1); }
                    th { background: #1a3c5e; color: white; padding: 12px 15px; text-align: left; }
                    td { padding: 11px 15px; border-bottom: 1px solid #eee; }
                    tr:hover td { background: #f5f9ff; }
                    .atrasado td { background: #fff3f3; }
                    .disp { color: #27ae60; font-weight: bold; }
                    .empr { color: #e74c3c; font-weight: bold; }
                    .atras { color: #e74c3c; font-weight: bold; font-size: .85em; }
                    form { background: white; padding: 20px; border-radius: 8px;
                           box-shadow: 0 2px 8px rgba(0,0,0,.1); display: flex; flex-wrap: wrap; gap: 10px; }
                    input { padding: 10px 14px; border: 1px solid #ccc; border-radius: 6px; font-size: .95em; flex: 1; min-width: 150px; }
                    button { background: #1a3c5e; color: white; border: none; padding: 10px 22px;
                             border-radius: 6px; cursor: pointer; font-size: .95em; }
                    button:hover { background: #2a5c8e; }
                    .forms { display: flex; gap: 20px; flex-wrap: wrap; }
                    .forms > div { flex: 1; min-width: 280px; }
                    .msg { padding: 20px 25px; border-radius: 8px; font-size: 1.1em; margin-bottom: 20px; }
                    .msg.ok   { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
                    .msg.erro { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
                    .btn { display: inline-block; background: #1a3c5e; color: white; padding: 10px 22px;
                           border-radius: 6px; text-decoration: none; }
                    .btn:hover { background: #2a5c8e; }
                    .btn-excluir { background: #c0392b; color: white; border: none; padding: 6px 14px;
                                           border-radius: 6px; cursor: pointer; font-size: .85em; }
                            .btn-excluir:hover { background: #e74c3c; }
                </style>
            </head>
            <body>
                <nav>
                    <span>📚 Biblioteca</span>
                    <a href='/'>Início</a>
                    <a href='/alunos'>Alunos</a>
                    <a href='/livros'>Livros</a>
                    <a href='/relatorio'>Relatórios</a>
                </nav>
                <div class='container'>%s</div>
            </body>
            </html>
        """.formatted(titulo, conteudo);
    }

    // ── UTILITÁRIOS HTTP ─────────────────────────────────────

    private static void responder(HttpExchange h, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        h.sendResponseHeaders(200, bytes.length);
        h.getResponseBody().write(bytes);
        h.getResponseBody().close();
    }

    private static String lerBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String,String> lerParams(String body) {
        Map<String,String> map = new HashMap<>();
        for (String par : body.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2)
                map.put(kv[0], java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return map;
    }
    private static void responderJson(HttpExchange h, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        h.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        h.sendResponseHeaders(200, bytes.length);
        h.getResponseBody().write(bytes);
        h.getResponseBody().close();
    }

}
