package org.example.system;

import org.example.product.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

/**
 *
 *
 */
public class ReaderXML {
    public static Stack<Product> read(InputStream inputStream) {
        Stack<Product> stack = new Stack<>();

        if (inputStream == null) {
            System.out.println("Input stream is null");
            return stack;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList productNodes = doc.getElementsByTagName("product");

            for (int i = 0; i < productNodes.getLength(); i++) {
                Element productElement = (Element) productNodes.item(i);
                try {
                    String name = getTextContent(productElement, "name");
                    if (name.isEmpty()) throw new IllegalArgumentException("Empty name");

                    Element coordElement = (Element) productElement.getElementsByTagName("coordinates").item(0);
                    int x = Integer.parseInt(getTextContent(coordElement, "x"));
                    long y = Long.parseLong(getTextContent(coordElement, "y"));
                    if (y > 696) throw new IllegalArgumentException("Y coordinate exceeds 696");
                    Coordinates coordinates = new Coordinates(x, y);

                    double price = Double.parseDouble(getTextContent(productElement, "price"));
                    if (price <= 0) throw new IllegalArgumentException("Invalid price");

                    UnitOfMeasure unit = UnitOfMeasure.valueOf(
                            getTextContent(productElement, "unitOfMeasure")
                    );

                    Person owner = parseOwner(productElement);

                    Product product = new Product(name, coordinates, price, unit, owner);

                    try {
                        ZonedDateTime creationDate = ZonedDateTime.parse(
                                getTextContent(productElement, "creationDate"),
                                DateTimeFormatter.ISO_ZONED_DATE_TIME
                        );
                        product.setCreationDate(creationDate);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Using auto-generated creation date for product " + name);
                    }

                    stack.push(product);
                } catch (Exception e) {
                    System.out.printf("Skipping product #%d: %s%n", i+1, e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading XML from input stream: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.out.println("Error closing input stream: " + e.getMessage());
            }
        }

        return stack;
    }

    private static Person parseOwner(Element productElement) {
        NodeList ownerNodes = productElement.getElementsByTagName("owner");
        if (ownerNodes.getLength() == 0) return null;

        Element ownerElement = (Element) ownerNodes.item(0);
        String ownerName = getTextContent(ownerElement, "name");
        if (ownerName.isEmpty()) throw new IllegalArgumentException("Owner name is empty");

        Integer height = parseOwnerInt(ownerElement, "height");
        Color hairColor = parseOwnerEnum(ownerElement, "hairColor", Color.class);
        Country nationality = Country.valueOf(getTextContent(ownerElement, "nationality"));

        return new Person(ownerName, height, hairColor, nationality);
    }

    private static <T extends Enum<T>> T parseOwnerEnum(Element element, String tagName, Class<T> enumClass) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return null;
        return Enum.valueOf(enumClass, nodes.item(0).getTextContent());
    }

    private static Integer parseOwnerInt(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return null;
        int value = Integer.parseInt(nodes.item(0).getTextContent());
        return value <= 0 ? null : value;
    }

    private static String getTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) throw new IllegalArgumentException("Missing " + tagName);
        return nodes.item(0).getTextContent();
    }

}