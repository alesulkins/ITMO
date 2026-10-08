package point;

import com.fastcgi.FCGIInterface;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        int failures = 0;

        while (true) {
            try {
                if (fcgi.FCGIaccept() < 0) {
                    break;
                }
                failures = 0;
            } catch (Throwable t) {
                if (++failures > 100) {
                    System.err.println("Слишком много ошибок приёма подряд, выходим: " + t);
                    break;
                }
                continue;
            }

            handle(System.nanoTime());
        }
    }

    private static void handle(long startedAt) {
        try {
            Properties params = FCGIInterface.request.params;
            String method = params.getProperty("REQUEST_METHOD", "");
            String query = params.getProperty("QUERY_STRING", "");

            switch (method) {
                case "GET":
                    if (query.isEmpty()) {
                        respond(200, success(startedAt));
                    } else {
                        check(query, startedAt);
                    }
                    break;

                case "DELETE":
                    Store.clear();
                    respond(200, success(startedAt));
                    break;

                default:
                    respond(405, error("Поддерживаются только GET и DELETE.", startedAt));
            }
        } catch (ValidationException e) {
            respond(400, error(e.getMessage(), startedAt));
        } catch (Throwable t) {
            respond(500, error("Внутренняя ошибка сервера: " + t, startedAt));
        }
    }

    private static void check(String query, long startedAt) throws ValidationException {
        Params point = new Params(query);
        boolean hit = point.isHit();

        long execNanos = System.nanoTime() - startedAt;

        Store.add(new Check(point.getX(), point.getY(), point.getR(), hit, System.currentTimeMillis(), execNanos));
        // вся история 
        respond(200, success(startedAt));
    }

    private static String success(long startedAt) {
        List<Check> history = Store.history();
        StringBuilder rows = new StringBuilder();

        for (int i = 0; i < history.size(); i++) {
            if (i > 0) {
                rows.append(',');
            }
            rows.append(history.get(i).toJson());
        }

        return "{\"ok\":true,"
                + "\"history\":[" + rows + "],"
                + "\"currentTime\":" + System.currentTimeMillis() + ","
                + "\"scriptNanos\":" + (System.nanoTime() - startedAt)
                + "}";
    }

    private static String error(String message, long startedAt) {
        return "{\"ok\":false,"
                + "\"error\":\"" + escape(message) + "\","
                + "\"currentTime\":" + System.currentTimeMillis() + ","
                + "\"scriptNanos\":" + (System.nanoTime() - startedAt)
                + "}";
    }

    private static void respond(int status, String json) {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 ").append(status).append(' ').append(reason(status)).append("\r\n")
                .append("Content-Type: application/json; charset=utf-8\r\n")
                .append("Content-Length: ").append(body.length).append("\r\n")
                .append("Cache-Control: no-store\r\n")
                .append("\r\n")
                .append(json);
        System.out.print(response);
        System.out.flush();
    }

    private static String reason(int status) {
        switch (status) {
            case 200: return "OK";
            case 400: return "Bad Request";
            case 405: return "Method Not Allowed";
            default:  return "Internal Server Error";
        }
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
