import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8199), 0);
        server.createContext("/", new InfoHandler());
        server.createContext("/stop", new StopHandler());
        server.setExecutor(null);
        System.out.println("Service1 instance started on port 8199");
        server.start();
    }

    static class StopHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                // Respond with "Stopping services"
                String response = "Stopping services";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

                // Stop all Docker containers
                Runtime.getRuntime().exec("docker compose down");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                // Add a 2-second delay to simulate load
                Thread.sleep(2000);

                String containerInfo = getContainerInfo();
                String service2Info = getService2Info();

                String response = "{\n" +
                        "  \"service1\": " + containerInfo + ",\n" +
                        "  \"service2\": " + service2Info + "\n" +
                        "}";

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                } catch (Exception ignored) {}
            }
        }

        private String getContainerInfo() throws Exception {
            String ip = InetAddress.getLocalHost().getHostAddress();

            // Run commands and escape outputs to avoid JSON formatting issues
            String processes = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("ps -ax").getInputStream()))
                    .lines().collect(Collectors.joining("\\n")).replace("\"", "\\\"");
            String diskSpace = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("df").getInputStream()))
                    .lines().collect(Collectors.joining("\\n")).replace("\"", "\\\"");
            String uptime = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("uptime").getInputStream()))
                    .lines().collect(Collectors.joining("\\n")).replace("\"", "\\\"");

            return "{\n" +
                    "    \"ip\": \"" + ip + "\",\n" +
                    "    \"processes\": \"" + processes + "\",\n" +
                    "    \"diskSpace\": \"" + diskSpace + "\",\n" +
                    "    \"uptime\": \"" + uptime + "\"\n" +
                    "  }";
        }

        private String getService2Info() {
            try {
                URL url = new URL("http://service2:5000/info");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String service2Response = in.lines().collect(Collectors.joining("\n"));
                in.close();

                return service2Response;
            } catch (Exception e) {
                System.err.println("Failed to contact Service2: " + e.getMessage());
                return "{ \"error\": \"Service2 unavailable\" }";
            }
        }

    }
}
