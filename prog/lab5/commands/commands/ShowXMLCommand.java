package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShowXMLCommand implements BaseCommand {

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager)
            throws WrongArgumentException {
        if (args.length > 0) {
            throw new WrongArgumentException("Команда show_xml не принимает аргументы.");
        }

        String filePath = System.getenv("FILE_LAB5");
        if (filePath == null) {
            throw new WrongArgumentException("Переменная окружения FILE_LAB5 не установлена.");
        }

        try {
            String xmlContent = Files.readString(Paths.get(filePath));
            System.out.println(xmlContent);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "show_xml";
    }

    @Override
    public String getDescription() {
        return "shows your collection";
    }
}
