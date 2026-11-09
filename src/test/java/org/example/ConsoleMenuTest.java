package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {
    @TempDir
    Path tempDir;

    @Test
    void showsAllHeroFieldsAndSavesRosterOnExit() throws IOException {
        Hero hero = new Hero("Aria", 12, "Легендарное", "Друиды", "Зелёный", true);
        Path dataFile = tempDir.resolve("heroes.json");
        HeroRepository repository = new HeroRepository(dataFile);
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ConsoleMenu menu = new ConsoleMenu(List.of(hero), repository,
                new Scanner("1\n0\n"), outputTo(capturedOutput));

        menu.run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Показать всех героев"));
        assertTrue(output.contains("Имя: Aria"));
        assertTrue(output.contains("Сила: 12"));
        assertTrue(output.contains("Редкость: Легендарное"));
        assertTrue(output.contains("Класс: Друиды"));
        assertTrue(output.contains("Стихия: Зелёный"));
        assertTrue(output.contains("Лекарь: Да"));
        assertEquals(List.of(hero), repository.load());
    }

    @Test
    void displaysMessageWhenRosterIsEmpty() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ConsoleMenu menu = new ConsoleMenu(List.of(), new HeroRepository(tempDir.resolve("heroes.json")),
                new Scanner("1\n0\n"), outputTo(capturedOutput));

        menu.run();

        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("Список героев пуст"));
    }

    @Test
    void explainsInvalidInputAndContinuesUntilExit() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ConsoleMenu menu = new ConsoleMenu(List.of(), new HeroRepository(tempDir.resolve("heroes.json")),
                new Scanner("abc\n2\n0\n"), outputTo(capturedOutput));

        menu.run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Введите 1 или 0"));
        assertEquals(3, output.split("Показать всех героев", -1).length - 1);
    }

    @Test
    void reportsSaveFailureWhenExiting() throws IOException {
        Path dataFile = Files.createDirectory(tempDir.resolve("heroes.json"));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        ConsoleMenu menu = new ConsoleMenu(List.of(), new HeroRepository(dataFile),
                new Scanner("0\n"), outputTo(capturedOutput));

        menu.run();

        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("Ошибка сохранения"));
    }

    private static PrintStream outputTo(ByteArrayOutputStream output) {
        return new PrintStream(output, true, StandardCharsets.UTF_8);
    }
}