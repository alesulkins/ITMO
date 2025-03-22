package org.example.system;

import org.example.product.Product;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;


public class WriterXML {

    public static void write(String path, List<Product> data) {
        File file = new File(path);

        if (!file.exists() || file.isDirectory() || !file.canWrite()) {
            System.out.println("Unable to write into the file: " + file.getAbsolutePath());
            return;
        }

        try (
                BufferedOutputStream fs = new BufferedOutputStream(new FileOutputStream(path));
                OutputStreamWriter output = new OutputStreamWriter(fs)
        ) {
            StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<products>\n");

            for (Product product : data) {
                xml.append("\t").append(product.toXML()).append("\n");
            }

            xml.append("</products>");

            output.write(xml.toString());
            output.flush();

            System.out.println("The data has been successfully written to the file: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error during writing into the file: " + file.getAbsolutePath());
            System.out.println(e.getMessage());
        }
    }
}