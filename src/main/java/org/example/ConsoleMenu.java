package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final List<Hero> heroes;
    private final HeroRepository repository;
    private final Scanner input;
    private final PrintStream output;

    public ConsoleMenu(List<Hero> heroes, HeroRepository repository, Scanner input, PrintStream output) {
        this.heroes = new ArrayList<>(heroes);
        this.repository = repository;
        this.input = input;
        this.output = output;
    }

    public void run() {
        while (true) {
            printMenu();
            if (!input.hasNextLine()) {
                saveAndExit();
                return;
            }

            String choice = input.nextLine().trim();
            switch (choice) {
                case "1" -> showHeroes();
                case "0" -> {
                    saveAndExit();
                    return;
                }
                default -> output.println("Неверный пункт. Введите 1 или 0.");
            }
        }
    }

    private void printMenu() {
        output.println();
        output.println("1 — Показать всех героев");
        output.println("0 — Выход");
        output.print("Выберите пункт: ");
    }

    private void showHeroes() {
        if (heroes.isEmpty()) {
            output.println("Список героев пуст.");
            return;
        }

        for (Hero hero : heroes) {
            output.printf("Имя: %s | Сила: %d | Редкость: %s | Класс: %s | Стихия: %s | Лекарь: %s%n",
                    hero.name(), hero.strength(), hero.rarity(), hero.heroClass(), hero.element(),
                    hero.healer() ? "Да" : "Нет");
        }
    }

    private void saveAndExit() {
        try {
            repository.save(heroes);
            output.println("Список героев сохранён.");
        } catch (IOException e) {
            output.println("Ошибка сохранения списка героев: " + e.getMessage());
        }
    }
}