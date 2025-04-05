package org.example.system;

import org.example.product.Person;
import org.example.product.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Stack;


public class WriterXML {

    public static void write(String path, Stack<Product> products) {
        if (products == null || path == null) {
            throw new IllegalArgumentException("Path and products must not be null");
        }

        try {
            // Создаем XML-документ
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Корневой элемент
            Element rootElement = doc.createElement("products");
            doc.appendChild(rootElement);

            // Добавляем все продукты
            for (Product product : products) {
                Element productElement = createProductElement(doc, product);
                rootElement.appendChild(productElement);
            }

            // Записываем документ в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));

            transformer.transform(source, result);

            System.out.println("Successfully wrote " + products.size() + " products to " + path);
        } catch (Exception e) {
            System.err.println("Error writing XML file: " + e.getMessage());
            throw new RuntimeException("Failed to write XML", e);
        }
    }

    private static Element createProductElement(Document doc, Product product) {
        Element productElement = doc.createElement("product");

        appendChildElement(doc, productElement, "id", product.getId().toString());

        appendChildElement(doc, productElement, "name", product.getName());

        Element coordinatesElement = doc.createElement("coordinates");
        appendChildElement(doc, coordinatesElement, "x", String.valueOf(product.getCoordinates().getX()));
        appendChildElement(doc, coordinatesElement, "y", String.valueOf(product.getCoordinates().getY()));
        productElement.appendChild(coordinatesElement);

        String formattedDate = product.getCreationDate().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        appendChildElement(doc, productElement, "creationDate", formattedDate);

        appendChildElement(doc, productElement, "price", String.valueOf(product.getPrice()));

        appendChildElement(doc, productElement, "unitOfMeasure", product.getUnitOfMeasure().name());

        if (product.getOwner() != null) {
            Element ownerElement = createOwnerElement(doc, product.getOwner());
            productElement.appendChild(ownerElement);
        }

        return productElement;
    }

    private static Element createOwnerElement(Document doc, Person owner) {
        Element ownerElement = doc.createElement("owner");

        appendChildElement(doc, ownerElement, "name", owner.getName());

        if (owner.getHeight() != null) {
            appendChildElement(doc, ownerElement, "height", owner.getHeight().toString());
        }

        if (owner.getHairColor() != null) {
            appendChildElement(doc, ownerElement, "hairColor", owner.getHairColor().name());
        }

        appendChildElement(doc, ownerElement, "nationality", owner.getNationality().name());

        return ownerElement;
    }

    private static void appendChildElement(Document doc, Element parent, String tagName, String textContent) {
        Element child = doc.createElement(tagName);
        child.setTextContent(textContent);
        parent.appendChild(child);
    }
}