package org.example.util;

import org.example.manager.CollectionManager;
import org.example.manager.CommandManager;
import org.example.product.*;
import org.example.system.WriterXML;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс Receiver отвечает за выполнение команд, связанных с коллекцией продуктов.
 * Он управляет взаимодействием с CommandManager и выполняет
 * операции с коллекцией - добавление, удаление и обновление элементов.
 * Также класс поддерживает выполнение скриптов и хранение истории команд.
 */

public class Receiver {
    private final CommandManager commandManager = new CommandManager();
    private final ArrayDeque<File> scriptStack = new ArrayDeque<>();

    /**
     * Сохраняет коллекцию в указанный файл.
     *
     * @param manager  Менеджер коллекции
     * @param fileName Имя файла для сохранения
     * @return Сообщение об успешном сохранении
     */
    public String save(CollectionManager manager, String fileName) {
        List<Product> products = new ArrayList<>(manager.getCollection());
        WriterXML.write(fileName, products);
        return "Collection has been successfully saved into the file: " + fileName;
    }

    /**
     * Выполняет скрипт команд из файла.
     *
     * @param manager  Менеджер коллекции
     * @param fileName Имя файла со скриптом
     * @return Результат выполнения скрипта
     */
    public String executeScript(CollectionManager manager, String fileName) {
        File scriptFile = new File(fileName);

        if (!scriptFile.canRead()) {
            return "Not enough rights to read the file: " + fileName;
        }
        if (scriptStack.contains(scriptFile)) {
            return "Recursion found";
        }
        scriptStack.push(scriptFile);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(scriptFile), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    System.out.println("Command executing: " + line);
                    switch (line){
                        case "add" :
                            String name = br.readLine();
                            int xValue = Integer.parseInt(br.readLine());
                            long yValue = Long.parseLong(br.readLine());
                            Coordinates coordinates = new Coordinates(xValue, yValue);
                            Double price = Double.parseDouble(br.readLine());
                            UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(br.readLine());
                            Person owner = null;
                            if (!br.readLine().equals("no")){
                                String ownerName = br.readLine();
                                int height = Integer.parseInt(br.readLine());
                                Color hair = Color.valueOf(br.readLine());
                                Country nationality = Country.valueOf(br.readLine());
                                owner = new Person(ownerName, height, hair, nationality);
                            }
                            Product product = new Product(name, coordinates, price, unitOfMeasure, owner);
                            manager.getCollection().add(product);
                    }
                    String result = commandManager.doCommand(line, manager, this);
                    //System.out.println(result);
                }
            }
            return "Script has been successfully executed.";
        } catch (IOException e) {
            return "Error during reading the file: " + e.getMessage();
        } finally {
            scriptStack.pop();
        }
    }

    /**
     * Выводит все продукты коллекции в порядке возрастания.
     *
     * @param manager Менеджер коллекции
     * @return Строка с отсортированными продуктами
     */
    public String printAscending(CollectionManager manager) {
        if (manager.getCollection().isEmpty()) {
            return "Коллекция пуста.";
        }

        return manager.getCollection().stream()
                .sorted()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Выводит владельцев продуктов, отсортированных по возрастанию.
     *
     * @param manager Менеджер коллекции
     * @return Строка с владельцами продуктов, отсортированными по возрастанию
     */
    public String printFieldAscendingOwner(CollectionManager manager) {
        if (manager.getCollection().isEmpty()) {
            return "Collection is empty.";
        }

        return manager.getCollection().stream()
                .filter(product -> product.getOwner() != null)
                .map(Product::getOwner)
                .sorted()
                .map(Person::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Обновляет продукт с заданным идентификатором в коллекции.
     *
     * @param manager Менеджер коллекции
     * @param id      Идентификатор продукта для обновления
     * @return Сообщение о результате обновления
     */
    public String updateId(CollectionManager manager, Long id) {
        Product newProduct = createProduct();
        for (Product product : manager.getCollection()) {
            if (product.getId().equals(id)) {
                newProduct.setId(id); // старый id
                manager.getCollection().remove(product);
                manager.getCollection().add(newProduct);
                return "Element with id " + id + " has been successfully added.";
            }
        }
        return "Element with id " + id + " is not found.";
    }

    /**
     * Выводит историю выполненных команд.
     *
     * @return Строка с историей последних 7 команд
     */
    public String history() {
        CommandManager commandManager = new CommandManager();
        Queue<String> history = commandManager.getCommandHistory();
        if (history.isEmpty()) {
            return "is empty";
        }
        StringBuilder historyString = new StringBuilder("Last 7 commands\n");
        for (String command : history) {
            historyString.append(command).append("\n");
        }
        return historyString.toString();
    }

    /**
     * Удаляет все продукты, превышающие заданный по параметрам.
     *
     * @param manager Менеджер коллекции
     * @param product Продукт для сравнения
     * @return Сообщение об успешном удалении
     */
    public String removeGreater(CollectionManager manager, Product product) {
        manager.getCollection().removeIf(p -> p.compareTo(product) > 0);
        return "All items exceeding the specified value have been successfully deleted.";
    }

    /**
     * Добавляет продукт в коллекцию, если его характеристики меньше минимального.
     *
     * @param manager Менеджер коллекции
     * @return Сообщение о результате добавления
     */
    public String addIfMin(CollectionManager manager) {
        Product newProduct = createProduct();
        if (manager.getCollection().isEmpty()) {
            manager.getCollection().add(newProduct);
            return "The item was added successfully because the collection was empty.";
        }
        Product minProduct = manager.getCollection().first();
        if (newProduct.compareTo(minProduct) < 0) {
            manager.getCollection().add(newProduct);
            return "The item was added successfully because it is less than the minimum.";
        }
        return "The element was not added because it is not less than the minimum.";
    }

    /**
     * Группирует элементы коллекции по идентификатору и выводит количество элементов с одинаковым id.
     *
     * @param manager Менеджер коллекции
     * @return Строка с группировкой по id
     */
    public String groupCountingById(CollectionManager manager) {
        Map<Long, Integer> idCountMap = new HashMap<>();

        for (Product product : manager.getCollection()) {
            Long id = product.getId();
            idCountMap.put(id, idCountMap.getOrDefault(id, 0) + 1);
        }

        StringBuilder result = new StringBuilder("Group by id:\n");
        for (Map.Entry<Long, Integer> entry : idCountMap.entrySet()) {
            result.append("id: ").append(entry.getKey())
                    .append(", amount: ").append(entry.getValue())
                    .append("\n");
        }

        return result.toString();
    }

    /**
     * Завершается выполнение программы.
     *
     * @return Пустая строка, так как программа завершена
     */
    public String exit() {
        System.out.println("completion of the program!");
        System.exit(0);
        return "";
    }

    /**
     * Очищает коллекцию.
     *
     * @param manager Менеджер коллекции
     * @return Сообщение о результатах выполнения
     */
    public String clear(CollectionManager manager) {
        manager.getCollection().clear();
        return "done! your collection is empty!";
    }

    /**
     * Выводит список доступных команд с их описаниями.
     *
     * @return Строка с перечнем всех доступных команд
     */
    public String help() {
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");
        commandManager.getCommands().forEach((command, baseCommand) -> helpMessage.append(command).append(" : ").append(baseCommand.getDescription()).append("\n"));
        return helpMessage.toString();
    }

    /**
     * Удаляет продукт с заданным id из коллекции.
     *
     * @param manager Менеджер коллекции
     * @param id      Идентификатор продукта
     * @return Сообщение о результате удаления
     */
    public String removeByIdProducts(CollectionManager manager, Long id) {
        for (Product product : manager.getCollection()) {
            if (product.getId().equals(id)) {
                manager.getCollection().remove(product);
                return "Element with id " + id + " has been successfully deleted.";
            }
        }
        return "Element with id " + id + " is not found.";
    }

    /**
     * Показывает все продукты из коллекции.
     *
     * @param manager Менеджер коллекции
     * @return Строка с представлением всех продуктов коллекции
     */
    public String showProducts(CollectionManager manager) {
        String products = "";
        for (Product product : manager.getCollection()) {
            products += product.toString() + "\n";
        }
        return products;
    }

    /**
     * Добавляет новый продукт в коллекцию.
     *
     * @param manager Менеджер коллекции
     * @return Сообщение о результате добавления
     */
    public String addProduct(CollectionManager manager) {
        Product product = createProduct();
        manager.getCollection().add(product);
        return "Element was successfully added into the collection";
    }

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
                System.out.println("");
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
                System.out.println("Incorrect input. Please, enter the number");
            }
        }

        System.out.println("Enter the unit of measure (CENTIMETERS, LITERS, MILLILITERS, MILLIGRAMS): ");
        String unitOfMeasureInput = scanner.nextLine().toUpperCase();
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(unitOfMeasureInput);
        //добавить проверку на корректный ввод
        System.out.println("Do you want to add the owner? (yes/no): ");
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