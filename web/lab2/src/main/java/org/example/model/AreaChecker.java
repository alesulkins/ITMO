package org.example.model;

import java.math.BigDecimal;

public class AreaChecker {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    public static boolean isInside(BigDecimal x, BigDecimal y, BigDecimal r) {
        if (x.compareTo(ZERO) >= 0 && y.compareTo(ZERO) >= 0) {
            return x.compareTo(r) <= 0 && y.compareTo(r.divide(TWO)) <= 0;
        }
        if (x.compareTo(ZERO) < 0 && y.compareTo(ZERO) > 0) {
            return false;
        }
        if (x.compareTo(ZERO) <= 0 && y.compareTo(ZERO) <= 0) {
            BigDecimal sumSquares = x.multiply(x).add(y.multiply(y));
            BigDecimal radius = r.multiply(r);
            return sumSquares.compareTo(radius) <= 0;
        }
        if (x.compareTo(ZERO) >= 0 && y.compareTo(ZERO) <= 0) {
            BigDecimal halfX = x.divide(TWO);
            BigDecimal halfR = r.divide(TWO);
            return y.compareTo(halfX.subtract(halfR)) >= 0;
        }
        return false;
    }
}
