package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HeroRepository repository = new HeroRepository(Path.of("data", "heroes.json"));
        try {
            List<Hero> heroes = repository.load();
            new ConsoleMenu(heroes, repository, new Scanner(System.in), System.out).run();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки списка героев: " + e.getMessage());
        }
    }
}