package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.WriterXML;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Класс команды "save", сохраняет коллекцию в файл.
 * Реализует интерфейс BaseCommand.
 */
public class SaveCommand implements BaseCommand {

    /**
     * Выполняет команду сохранения коллекции в файл.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды - имя файла для сохранения
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        // if filePath - save to it
        String filePath = (args.length > 0) ? args[0] : null;

        // 2. no filePath - save to env
        if (filePath == null) {
            filePath = System.getenv("FILE_LAB5"); // или другой переменной
        }

        // 3. no file path
        if (filePath == null) {
            throw new WrongArgumentException(
                    "File path not specified. " +
                            "Either pass it as argument (save file.xml) " +
                            "or set LAB5_FILE environment variable."
            );
        }

        WriterXML.write(filePath, manager.getCollection());    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "save"
     */
    @Override
    public String getName() {
        return "save";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "Сохраняет коллекцию в файл"
     */
    @Override
    public String getDescription() {
        return "saves collection into the file";
    }

}