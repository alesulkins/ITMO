package org.example.system;

import com.sun.source.tree.Tree;
import org.example.product.*;
import org.example.util.IdGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ReaderXML {


    public static TreeSet<Product> read(String path) throws Exception {

        if (path == null || path .isEmpty()) {
            throw new Exception("The file was not found or is not readable: " + path);
        }

        File file = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));        String line;
        StringBuilder fileContent = new StringBuilder();
        while ((line = br.readLine()) != null) {
            fileContent.append(line);
        }
        if (fileContent.isEmpty()){
            System.out.println("Your collection is empty");
            return new TreeSet<>();
        }

        TreeSet<Product> products= new TreeSet<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList productList = doc.getElementsByTagName("product");
            if (productList.getLength() == 0) {
                System.out.println("No product found in the file");
                return products;
            }

            for (int i = 0; i < productList.getLength(); i++) {
                Node node = productList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element productElement = (Element) node;

                    Product product = parseProduct(productElement);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
            System.out.println("Найдено продуктов: " + products.size());
        } catch (IOException e) {
            throw new Exception("Error during reading the file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Unknown error during file processing: " + e.getMessage(), e);
        }
        return products;
    }


    private static Product parseProduct(Element productElement) {
        try {
            Long id = Long.parseLong(getElementText(productElement, "id"));
            IdGenerator.setIdCounter(Math.max(id,IdGenerator.getIdCounter())+1);
            String name = getElementText(productElement, "name");
            Coordinates coordinates = parseCoordinates(productElement);
            ZonedDateTime creationDate = ZonedDateTime.parse(getElementText(productElement, "creationDate"), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            Double price = Double.parseDouble(getElementText(productElement, "price"));
            UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(getElementText(productElement, "unitOfMeasure"));

            Person owner = null;
            NodeList ownerList = productElement.getElementsByTagName("owner");
            if (ownerList.getLength() > 0) {
                Element ownerElement = (Element) ownerList.item(0);
                owner = parseOwner(ownerElement);
                System.out.println(owner.toString());
            }
            Product product = new Product(name, coordinates, price, unitOfMeasure, owner);
            product.setId(id);
            product.setCreationDate(creationDate);
            return product;
        } catch (Exception e) {
            System.out.println("An error occurred while parsing the script: " + e.getMessage());
            return null;
        }
    }

    private static Coordinates parseCoordinates(Element productElement) {
        Element coordinatesElement = (Element) productElement.getElementsByTagName("coordinates").item(0);
        int x = Integer.parseInt(getElementText(coordinatesElement, "x"));
        long y = Long.parseLong(getElementText(coordinatesElement, "y"));
        return new Coordinates(x, y);
    }

    private static Person parseOwner(Element ownerElement) {
        String name = getElementText(ownerElement, "name");
        Integer height = null;
        if (ownerElement.getElementsByTagName("height").getLength() > 0) {
            height = Integer.parseInt(getElementText(ownerElement, "height"));
        }
        Color hairColor = null;
        if (ownerElement.getElementsByTagName("hairColor").getLength() > 0) {
            hairColor = Color.valueOf(getElementText(ownerElement, "hairColor"));
        }
        Country nationality = Country.valueOf(getElementText(ownerElement, "nationality"));
        return new Person(name, height, hairColor, nationality);
    }

    private static String getElementText(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }
}