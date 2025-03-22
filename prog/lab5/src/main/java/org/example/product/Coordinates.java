package org.example.product;

public class Coordinates {
    private int x;
    private long y; // max 696


    public Coordinates(int x, long y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        if (y > 696) {
            throw new IllegalArgumentException("The Y-value cannot be more than 696");
        }
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}