package point;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Params {

    private static final String[] X_VALUES = {"-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3"};
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal Y_MIN = new BigDecimal("-3");
    private static final BigDecimal Y_MAX = new BigDecimal("3");
    private static final BigDecimal R_MIN = new BigDecimal("2");
    private static final BigDecimal R_MAX = new BigDecimal("5");
    private static final Pattern NUMBER = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal r;

    public Params(String body) throws ValidationException {
        if (body == null || body.isEmpty()) {
            throw new ValidationException("Пустой запрос: нет данных формы.");
        }

        Map<String, String> fields = split(body);
        this.x = readX(fields.getOrDefault("x", "").trim());
        this.y = readY(fields.getOrDefault("y", "").trim());
        this.r = readR(fields.getOrDefault("r", "").trim());
    }

    private static BigDecimal readX(String text) throws ValidationException {
        if (text.isEmpty()) {
            throw new ValidationException("Координата X не передана.");
        }
        if (!NUMBER.matcher(text).matches()) {
            throw new ValidationException("X должен быть числом.");
        }

        BigDecimal x = new BigDecimal(text);

        for (String allowed : X_VALUES) {
            if (x.compareTo(new BigDecimal(allowed)) == 0) {
                return x;
            }
        }

        throw new ValidationException("Выберите X из предложенных значений.");
    }

    private static BigDecimal readY(String text) throws ValidationException {
        String normalized = text.replace(',', '.').replace('−', '-');

        if (normalized.isEmpty()) {
            throw new ValidationException("Введите Y — число строго между -3 и 3.");
        }
        if (!NUMBER.matcher(normalized).matches()) {
            throw new ValidationException("Y должен быть числом, например 1.5 или -2.");
        }

        BigDecimal exact = new BigDecimal(normalized);

        if (exact.compareTo(Y_MIN) <= 0 || exact.compareTo(Y_MAX) >= 0) {
            throw new ValidationException("Y выходит за границы: нужно строго между -3 и 3.");
        }

        return exact;
    }

    private static BigDecimal readR(String text) throws ValidationException {
        String normalized = text.replace(',', '.').replace('−', '-');

        if (normalized.isEmpty()) {
            throw new ValidationException("Введите R — число строго между 2 и 5.");
        }
        if (!NUMBER.matcher(normalized).matches()) {
            throw new ValidationException("R должен быть числом, например 2.5.");
        }

        BigDecimal exact = new BigDecimal(normalized);

        if (exact.compareTo(R_MIN) <= 0 || exact.compareTo(R_MAX) >= 0) {
            throw new ValidationException("R выходит за границы: нужно строго между 2 и 5.");
        }

        return exact;
    }

    private static Map<String, String> split(String body) {
        Map<String, String> result = new HashMap<>();

        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');

            if (eq > 0) {
                result.put(decode(pair.substring(0, eq)), decode(pair.substring(eq + 1)));
            }
        }

        return result;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }

    public boolean isHit() {
        BigDecimal half = r.divide(TWO);

        boolean inRectangle = x.compareTo(r.negate()) >= 0 && x.signum() <= 0
                && y.compareTo(half.negate()) >= 0 && y.signum() <= 0;

        boolean inTriangle = x.signum() >= 0 && y.signum() >= 0
                && x.add(y).compareTo(half) <= 0;

        boolean inCircle = x.signum() >= 0 && y.signum() <= 0
                && x.multiply(x).add(y.multiply(y)).compareTo(r.multiply(r)) <= 0;

        return inRectangle || inTriangle || inCircle;
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
