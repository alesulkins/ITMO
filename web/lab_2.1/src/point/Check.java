package point;

import java.math.BigDecimal;

public class Check {

    public final BigDecimal x;
    public final BigDecimal y;
    public final BigDecimal r;
    public final boolean hit;
    public final long time;
    public final long execNanos;

    public Check(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, long time, long execNanos) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = time;
        this.execNanos = execNanos;
    }

    public String toJson() {
        return "{\"x\":\"" + number(x)
                + "\",\"y\":\"" + number(y)
                + "\",\"r\":\"" + number(r)
                + "\",\"hit\":" + hit
                + ",\"time\":" + time
                + ",\"execNanos\":" + execNanos + "}";
    }

    private static String number(BigDecimal value) {
        return value.signum() == 0 ? "0" : value.stripTrailingZeros().toPlainString();
    }
}
