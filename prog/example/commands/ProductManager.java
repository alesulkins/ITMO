package org.example.commands;

import org.example.product.*;

import java.util.Scanner;

public class ProductManager {
    /**
     * Запрашивает у пользователя информацию для создания нового продукта.
     *
     * @return Новый продукт
     */
    public Product createProduct() {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        while (true) {
            System.out.println("Enter the product's name: ");
            name = scanner.nextLine();
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("The name cannot be null. Please, try again.");
        }
        System.out.println("Enter the x coordinate (integer): ");
        int x = Integer.parseInt(scanner.nextLine());
        long y;
        while (true) {
            try{
                System.out.println("Enter the y coordinate (no more than 696): ");
                y = Long.parseLong(scanner.nextLine());
                if (y <= 696) {
                    break;
                } else {
                    System.out.println("The Y-value cannot be more than 696. Please, try again.");
                }
            } catch (Exception e){
                System.out.println("Incorrect input. Please, enter a valid number");
            }

        }
        Coordinates coordinates = new Coordinates(x, y);

        double price = 0;
        while (true) {
            System.out.println("Enter the product price ");
            try {
                price = Double.parseDouble(scanner.nextLine());
                if (price > 0) {
                    break;
                }
                System.out.println("The price should be more than 0. Please, try again");
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input. Please, enter a correct number");
            }
        }

        System.out.println("Enter the unit of measure (CENTIMETERS, LITERS, MILLILITERS, MILLIGRAMS): ");
        String unitOfMeasureInput = scanner.nextLine().toUpperCase();
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(unitOfMeasureInput);
        //добавить проверку на корректный ввод
        System.out.println("Do you want to addProduct the owner? (yes/no): ");
        String addOwnerInput = scanner.nextLine().toLowerCase();
        Person owner = null;
        if (addOwnerInput.equals("yes")) {
            System.out.println("Enter the owner's name ");
            String ownerName = scanner.nextLine();
            if (ownerName.isEmpty()) {
                throw new IllegalArgumentException("Owner name cannot be null.");
            }

            System.out.println("Enter the owner's height: ");
            String heightInput = scanner.nextLine();
            Integer height = heightInput.isEmpty() ? null : Integer.parseInt(heightInput);
            if (height != null && height <= 0) {
                throw new IllegalArgumentException("Height should be more than 0.");
            }

            System.out.println("Enter the owner's hair-color (GREEN, BLACK, BROWN, или оставьте пустым): ");
            String hairColorInput = scanner.nextLine().toUpperCase();
            Color hairColor = hairColorInput.isEmpty() ? null : Color.valueOf(hairColorInput);

            System.out.println("Enter the owner's nationality (RUSSIA, FRANCE, INDIA, ITALY, NORTH_KOREA): ");
            String nationalityInput = scanner.nextLine().toUpperCase();
            Country nationality = Country.valueOf(nationalityInput);

            owner = new Person(ownerName, height, hairColor, nationality);
        }

        return new Product(name, coordinates, price, unitOfMeasure, owner);
    }
}
