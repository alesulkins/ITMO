package org.example.client;

import org.example.shared.product.*;
import org.example.shared.product.Product;
import java.util.Scanner;

public class ProductManager {
    /**
     * Запрашивает у пользователя информацию для создания нового продукта.
     *
     * @return Новый продукт
     */
    public static Product createProduct() {
        Scanner scanner = new Scanner(System.in);

        String name = "";
        while (true) {
            System.out.println("Enter the product's name: ");
            name = scanner.nextLine();
            if (!name.isEmpty()) break;
            System.out.println("The name cannot be null. Please, try again.");
        }

        System.out.println("Enter the x coordinate (integer): ");
        int x = Integer.parseInt(scanner.nextLine());

        long y;
        while (true) {
            try {
                System.out.println("Enter the y coordinate (no more than 696): ");
                y = Long.parseLong(scanner.nextLine());
                if (y <= 696) break;
                System.out.println("The Y-value cannot be more than 696. Please, try again.");
            } catch (Exception e) {
                System.out.println("Incorrect input. Please, enter a valid number.");
            }
        }

        Coordinates coordinates = new Coordinates(x, y);

        double price = 0;
        while (true) {
            System.out.println("Enter the product price: ");
            try {
                price = Double.parseDouble(scanner.nextLine());
                if (price > 0) break;
                System.out.println("The price should be more than 0. Please, try again.");
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input. Please, enter a correct number.");
            }
        }

        UnitOfMeasure unitOfMeasure = null;
        while (unitOfMeasure == null) {
            try {
                System.out.println("Enter the unit of measure (CENTIMETERS, LITERS, MILLILITERS, MILLIGRAMS): ");
                String input = scanner.nextLine().trim().toUpperCase();
                unitOfMeasure = UnitOfMeasure.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid unit! Available values: CENTIMETERS, LITERS, MILLILITERS, MILLIGRAMS");
            }
        }

        // 🧍 Владелец: всегда спрашиваем
        String ownerName = "";
        while (true) {
            System.out.println("Enter the owner's name: ");
            ownerName = scanner.nextLine();
            if (!ownerName.isEmpty()) break;
            System.out.println("Owner name cannot be empty. Please, try again.");
        }

        Integer height = null;
        while (true) {
            try {
                System.out.println("Enter the owner's height (must be > 0): ");
                String heightInput = scanner.nextLine();
                height = Integer.parseInt(heightInput);
                if (height <= 0) {
                    System.out.println("Height must be greater than 0. Try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
            }
        }

        Color hairColor = null;
        while (hairColor == null) {
            try {
                System.out.println("Enter the owner's hair-color (GREEN, BLACK, BROWN): ");
                String input = scanner.nextLine().toUpperCase().trim();
                hairColor = Color.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color! Available values: GREEN, BLACK, BROWN");
            }
        }

        Country nationality = null;
        while (nationality == null) {
            try {
                System.out.println("Enter the owner's nationality (RUSSIA, FRANCE, INDIA, ITALY, NORTH_KOREA): ");
                String input = scanner.nextLine().toUpperCase().trim();
                nationality = Country.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid country! Available values: RUSSIA, FRANCE, INDIA, ITALY, NORTH_KOREA");
            }
        }

        Person owner = new Person(ownerName, height, hairColor, nationality);

        return new Product(name, coordinates, price, unitOfMeasure, owner);
    }
}
