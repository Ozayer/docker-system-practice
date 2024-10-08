import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

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
        // Create an HTTP server that listens on port 8199
        HttpServer server = HttpServer.create(new InetSocketAddress(8199), 0);
        server.createContext("/", new InfoHandler());
        server.setExecutor(null); // Use the default executor
        System.out.println("Server started on port 8199");
        server.start();
    }

    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                // Retrieve container information and Service2 information
                String containerInfo = getContainerInfo();
                String service2Info = getService2Info();

                // Combine both pieces of information into a single JSON string
                String response = "{\n" +
                        "  \"service1\": " + containerInfo + ",\n" +
                        "  \"service2\": " + service2Info + "\n" +
                        "}";

                // Send the response headers (200 OK)
                exchange.sendResponseHeaders(200, response.getBytes().length);

                // Send the response body
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                } catch (Exception ignored) {}
            }
        }

        private String getContainerInfo() throws Exception {
            // Retrieve the container's IP address
            String ip = InetAddress.getLocalHost().getHostAddress();

            // Retrieve the list of running processes
            String processes = executeCommand("ps -ax");

            // Retrieve the available disk space
            String diskSpace = executeCommand("df");

            // Retrieve the time since the last boot
            String uptime = executeCommand("uptime");

            // Return the information in JSON format (manually construct JSON)
            return "{\n" +
                    "    \"ip\": \"" + ip + "\",\n" +
                    "    \"processes\": \"" + processes.replaceAll("\"", "\\\\\"") + "\",\n" +
                    "    \"diskSpace\": \"" + diskSpace.replaceAll("\"", "\\\\\"") + "\",\n" +
                    "    \"uptime\": \"" + uptime.replaceAll("\"", "\\\\\"") + "\"\n" +
                    "  }";
        }

        private String executeCommand(String command) throws Exception {
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }

        private String getService2Info() {
            try {
                // URL of Service2 (running on port 5000 in the Docker network)
                URL url = new URL("http://service2:5000/info");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Retrieve the response from Service2
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return in.lines().collect(Collectors.joining("\n"));
                }
            } catch (Exception e) {
                System.err.println("Failed to contact Service2: " + e.getMessage());
                // Return error information in JSON format
                return "{ \"error\": \"Service2 unavailable\" }";
            }
        }
    }
}
