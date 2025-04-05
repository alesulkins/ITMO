package org.example.product.comparator;

import org.example.product.Coordinates;
import org.example.product.Product;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Product> {
    private final Product referenceProduct;
    private final double referenceDistance;

    public DistanceComparator(Product referenceProduct) {
        this.referenceProduct = referenceProduct;
        this.referenceDistance = calculateDistance(referenceProduct.getCoordinates());
    }

    @Override
    public int compare(Product o1, Product o2) {
        double distance1 = calculateDistance(o1.getCoordinates());
        double distance2 = calculateDistance(o2.getCoordinates());
        return Double.compare(distance1, distance2);
    }
    private static double calculateDistance(Coordinates coords) {
        return Math.hypot(coords.getX(), coords.getY());
    }
    public boolean isGreater(Product product) {
        return calculateDistance(product.getCoordinates()) > referenceDistance;
    }
}
