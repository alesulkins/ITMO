package org.example.model;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Params {
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal r;

    public Params(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Отсутствует строка запроса");
        }
        var params = splitQuery(query);
        validateParams(params, false);
        this.x = new BigDecimal(params.get("x"));
        this.y = new BigDecimal(params.get("y"));
        this.r = new BigDecimal(params.get("r"));
    }

    public static Params fromRequest(HttpServletRequest req) throws ValidationException {
        return fromRequest(req, false);
    }

    public static Params fromRequest(HttpServletRequest req, boolean allowAnyX) throws ValidationException {
        Map<String, String> params = new HashMap<>();
        params.put("x", req.getParameter("x"));
        params.put("y", req.getParameter("y"));
        params.put("r", req.getParameter("r"));
        validateParams(params, allowAnyX);
        BigDecimal x = new BigDecimal(params.get("x"));
        BigDecimal y = new BigDecimal(params.get("y"));
        BigDecimal r = new BigDecimal(params.get("r"));
        return new Params(x, y, r);
    }

    private Params(BigDecimal x, BigDecimal y, BigDecimal r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    private static Map<String, String> splitQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length >= 1 && parts[0] != null && !parts[0].isEmpty())
                .collect(Collectors.toMap(
                        pairParts -> URLDecoder.decode(pairParts[0], StandardCharsets.UTF_8),
                        pairParts -> pairParts.length == 2 ? URLDecoder.decode(pairParts[1], StandardCharsets.UTF_8) : "",
                        (a, b) -> b,
                        HashMap::new
                ));
    }

    private static void validateParams(Map<String, String> params, boolean allowAnyX) throws ValidationException {
        var x = params.get("x");
        if (isBlank(x)) {
            throw new ValidationException("X недействителен");
        }
        var normalizedX = normalizeNumber(x);

        try {
            var xx = new BigDecimal(normalizedX);
            if (!allowAnyX) {
                var normalized = xx.stripTrailingZeros();
                if (normalized.scale() > 0) {
                    throw new ValidationException("X должен быть целым числом");
                }
                if (xx.compareTo(BigDecimal.valueOf(-3)) < 0 || xx.compareTo(BigDecimal.valueOf(5)) > 0) {
                    throw new ValidationException("X - некорректное значение");
                }
            }
            params.put("x", normalizedX);
        } catch (NumberFormatException e) {
            throw new ValidationException("X не является числом");
        }

        var y = params.get("y");
        if (isBlank(y)) {
            throw new ValidationException("Y недействителен");
        }
        var normalizedY = normalizeNumber(y);

        try {
            var yy = new BigDecimal(normalizedY);
            if (!(yy.compareTo(BigDecimal.valueOf(-5)) > 0 && yy.compareTo(BigDecimal.valueOf(3)) < 0)) {
                throw new ValidationException("Y - некорректное значение");
            }
            params.put("y", normalizedY);
        } catch (NumberFormatException e) {
            throw new ValidationException("Y не является числом");
        }

        var r = params.get("r");
        if (isBlank(r)) {
            throw new ValidationException("R недействителен");
        }
        var normalizedR = normalizeNumber(r);

        try {
            var rr = new BigDecimal(normalizedR);
            if (!(rr.compareTo(BigDecimal.valueOf(2)) > 0 && rr.compareTo(BigDecimal.valueOf(5)) < 0)) {
                throw new ValidationException("R - некорректное значение");
            }
            params.put("r", normalizedR);
        } catch (NumberFormatException e) {
            throw new ValidationException("R не является числом");
        }
    }

    private static String normalizeNumber(String value) {
        return value == null ? null : value.trim().replace(',', '.');
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getR() {
        return r;
    }
}
