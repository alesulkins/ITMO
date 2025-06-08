package org.example.shared.product;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private final int x;
    private final long y;

    public Coordinates(int x, long y) {
        if (y > 696) {
            throw new IllegalArgumentException("Y coordinate must be <= 696");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}