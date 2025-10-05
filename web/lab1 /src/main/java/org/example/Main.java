package org.example;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {
    private static final String FCGI_HEADERS = """
            Status: 200 OK
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String FCGI_ERROR_HEADERS = """
            Status: 400 Bad Request
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "time": "%s",
                "now": "%s",
                "result": %b
            }
            """;
    private static final String ERROR_JSON = """
            {
                "now": "%s",
                "reason": "%s"
            }
            """;

    public static void main(String[] args) {
        var fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            try {
                var queryParams = System.getProperties().getProperty("QUERY_STRING");
                var params = new Params(queryParams);

                var startTime = Instant.now();
                var result = calculate(params.getX(), params.getY(), params.getR());
                var endTime = Instant.now();

                var json = String.format(RESULT_JSON, ChronoUnit.NANOS.between(startTime, endTime), LocalDateTime.now(), result);
                var response = String.format(FCGI_HEADERS, json.getBytes(StandardCharsets.UTF_8).length + 2, json);
                System.out.println(response);
            } catch (ValidationException e) {
                var json = String.format(ERROR_JSON, LocalDateTime.now(), e.getMessage());
                var response = String.format(FCGI_ERROR_HEADERS, json.getBytes(StandardCharsets.UTF_8).length + 2, json);
                System.out.println(response);
            }
        }
    }

    private static boolean calculate(float x, float y, int r) {
        if (x < 0 && y > 0) {
            return (x * x + y * y) <= (r * r);
        }

        if (x > 0 && y > 0) {
            return (x >= 0 && x <= r / 2.0f) && (y >= 0 && y <= r);
        }

        if (x > 0 && y < 0) {
            return false;
        }

        if (x < 0 && y < 0) {
            return (-r <= x && x <= 0) && (-r <= y && y <= 0) && (y >= -x - r);
        }

        if (Math.abs(y) < 1e-6) {
            if (x < 0) {
                return (x * x) <= (r * r) || (x >= -r && x <= 0);
            } else if (x > 0) {
                return (x >= 0 && x <= r / 2.0f);
            } else {
                return true;
            }
        }
        if (Math.abs(x) < 1e-6) {
            if (y > 0) {
                return (y * y) <= (r * r) || (y >= 0 && y <= r);
            } else if (y < 0) {
                return (y >= -r && y <= 0);
            } else {
                return true;
            }
        }
        return false;
    }
}