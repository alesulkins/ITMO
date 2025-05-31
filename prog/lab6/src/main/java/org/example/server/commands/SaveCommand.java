package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.server.system.WriterXML;


public class SaveCommand {
    CollectionManager cm;
    public SaveCommand(CollectionManager cm) {
        this.cm = cm;
    }

    public String execute() {
        String filePath = System.getenv("FILE_LAB5");
        if (filePath == null) {
            return "Error: File path not specified. Set FILE_LAB5 environment variable.";
        }
        WriterXML.write(filePath, cm.getCollection());
        return "Collection saved to " + filePath;
    }

}


