package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HeroRepository {
    private static final Type HERO_LIST_TYPE = new TypeToken<List<Hero>>() { }.getType();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataFile;

    public HeroRepository(Path dataFile) {
        this.dataFile = dataFile;
    }

    public List<Hero> load() throws IOException {
        if (Files.notExists(dataFile)) {
            initializeFromTemplate();
        }

        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            List<Hero> heroes = GSON.fromJson(reader, HERO_LIST_TYPE);
            if (heroes == null) {
                throw new IOException("Файл списка героев должен содержать JSON-массив: " + dataFile);
            }
            return heroes;
        } catch (JsonParseException | IllegalStateException e) {
            throw new IOException("Не удалось прочитать список героев из " + dataFile, e);
        }
    }

    public void save(List<Hero> heroes) throws IOException {
        createParentDirectories();
        try (Writer writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8)) {
            GSON.toJson(heroes, HERO_LIST_TYPE, writer);
        }
    }

    private void initializeFromTemplate() throws IOException {
        createParentDirectories();
        try (InputStream template = HeroRepository.class.getResourceAsStream("/heroes.json")) {
            if (template == null) {
                throw new IOException("Не найден шаблон списка героев /heroes.json");
            }
            try {
                Files.copy(template, dataFile);
            } catch (FileAlreadyExistsException ignored) {
                // Another process initialized the file after the existence check.
            }
        }
    }

    private void createParentDirectories() throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}