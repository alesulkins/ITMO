package org.example;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class Params {
    private final float x;
    private final float y;
    private final int r;

    public Params(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Отсутствует строка запроса");
        }
        var params = splitQuery(query);
        validateParams(params);
        this.x = Float.parseFloat(params.get("x"));
        this.y = Float.parseFloat(params.get("y"));
        this.r = Integer.parseInt(params.get("r"));
    }

    private static Map<String, String> splitQuery(String query) {
        return Arrays.stream(query.split("&")).map(pair -> pair.split("=")).collect(Collectors.toMap(
                        pairParts -> URLDecoder.decode(pairParts[0], StandardCharsets.UTF_8),
                        pairParts -> URLDecoder.decode(pairParts[1], StandardCharsets.UTF_8),
                        (a, b) -> b,
                        HashMap::new
                )
        );
    }


    private static void validateParams(Map<String, String> params) throws ValidationException {
        var x = params.get("x");
        if (x == null || x.isEmpty()) {
            throw new ValidationException("X недействителен");
        }

        try {
            var xx = Float.parseFloat(x);
            if (xx < -2 || xx > 2) {
                throw new ValidationException("X - некорректное значение");
            }
            float[] allowed = {-2f, -1.5f, -1f, -0.5f, 0f, 0.5f, 1f, 1.5f, 2f};
            boolean found = false;
            for (float v : allowed) {
                if (Math.abs(xx - v) < 1e-6) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ValidationException("X - некорректное значение");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("X не является числом");
        }

        var y = params.get("y");
        if (y == null || y.isEmpty()) {
            throw new ValidationException("Y недействителен");
        }

        try {
            var yy = Float.parseFloat(y);
            if (yy <= -3 || yy >= 3) {
                throw new ValidationException("Y - некорректное значение");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Y не является числом");
        }

        var r = params.get("r");
        if (r == null || r.isEmpty()) {
            throw new ValidationException("R недействителен");
        }

        try {
            var rr = Integer.parseInt(r);
            int[] allowed = {1, 2, 3, 4, 5};
            boolean found = false;
            for (int v : allowed) {
                if (rr == v) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ValidationException("R - некорректное значение");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("R не является числом");
        }
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getR() {
        return r;
    }
}
