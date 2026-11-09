package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeroRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void initializesMissingRosterFromBundledEmptyTemplate() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");

        List<Hero> heroes = new HeroRepository(dataFile).load();

        assertEquals(List.of(), heroes);
        assertEquals("[]", Files.readString(dataFile).trim());
    }

    @Test
    void loadsExistingRosterWithoutReplacingIt() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");
        String json = "[{\"name\":\"Aria\",\"strength\":12,\"rarity\":\"Легендарное\",\"heroClass\":\"Друиды\",\"element\":\"Зелёный\",\"healer\":true}]";
        Files.writeString(dataFile, json);

        List<Hero> heroes = new HeroRepository(dataFile).load();

        assertEquals(List.of(new Hero("Aria", 12, "Легендарное", "Друиды", "Зелёный", true)), heroes);
        assertEquals(json, Files.readString(dataFile));
    }

    @Test
    void saveThenLoadPreservesEveryHeroField() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");
        List<Hero> heroes = List.of(new Hero("Aria", 12, "Легендарное", "Друиды", "Зелёный", true));
        HeroRepository repository = new HeroRepository(dataFile);

        repository.save(heroes);

        assertEquals(heroes, repository.load());
    }

    @Test
    void reportsMalformedJsonInsteadOfReplacingUserData() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");
        String malformedJson = "{broken";
        Files.writeString(dataFile, malformedJson);

        assertThrows(IOException.class, () -> new HeroRepository(dataFile).load());
        assertEquals(malformedJson, Files.readString(dataFile));
    }

    @Test
    void rejectsNullJsonWithoutReplacingUserData() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");
        String nullJson = "null";
        Files.writeString(dataFile, nullJson);

        assertThrows(IOException.class, () -> new HeroRepository(dataFile).load());
        assertEquals(nullJson, Files.readString(dataFile));
    }

    @Test
    void rejectsEmptyFileWithoutReplacingUserData() throws IOException {
        Path dataFile = tempDir.resolve("heroes.json");
        String emptyContent = "";
        Files.writeString(dataFile, emptyContent);

        assertThrows(IOException.class, () -> new HeroRepository(dataFile).load());
        assertEquals(emptyContent, Files.readString(dataFile));
    }

    @Test
    void reportsSaveFailureWhenDestinationIsADirectory() throws IOException {
        Path dataFile = Files.createDirectory(tempDir.resolve("heroes.json"));

        assertThrows(IOException.class, () -> new HeroRepository(dataFile).save(List.of()));
    }
}