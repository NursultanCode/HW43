package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    private static HttpServer makeServer() throws IOException {

        String host = "localhost";

        InetSocketAddress address = new InetSocketAddress(host,
                9000);
        String msg = "запускаем сервер по адресу"
                + " http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("  удачно!");
        return server;
    }

    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            server.start();
            initRoutes(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleMain);
        server.createContext("/apps", Main::handleApps);
        server.createContext("/apps/profile", Main::handleAppsProfile);
        server.createContext("/index.html",Main::handleIndex);
    }

    private static void handleIndex(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
// получаем экземпляр класса PrintWriter, который
// умеет записывать в поток текстовые данные
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                File file = new File("src/server/site/index.html");
                System.out.println(file.toPath());
                exchange.sendResponseHeaders(200,file.length());
                try (OutputStream os = exchange.getResponseBody()){
                    Files.copy(file.toPath(),os);
                }
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleMain(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
// получаем экземпляр класса PrintWriter, который
// умеет записывать в поток текстовые данные
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext()
                        .getPath();
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса",
                        exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleApps(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
// получаем экземпляр класса PrintWriter, который
// умеет записывать в поток текстовые данные
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext()
                        .getPath();
                writer.write("Here will be app collections");
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAppsProfile(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
// получаем экземпляр класса PrintWriter, который
// умеет записывать в поток текстовые данные
            try (PrintWriter writer = (PrintWriter) getWriterFrom(exchange)) {
                writer.write("Here will be Profile information");
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Writer getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) ->
                write(writer, "\t" + k, v.toString())
        );
    }

    private static BufferedReader getReader(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    private static void writeData(Writer writer, HttpExchange exchange) {
        try (BufferedReader reader = getReader(exchange)) {
            if (!reader.ready()) {
                return;
            }
            write(writer, "Блок данных", "");
            reader.lines().forEach(v -> write(writer, "\t", v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}