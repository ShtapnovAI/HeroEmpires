package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeroTest {
    @Test
    void storesAllHeroFieldsIncludingName() {
        Hero hero = new Hero("Aria", 12, "Легендарное", "Друиды", "Зелёный", true);

        assertAll(
                () -> assertEquals("Aria", hero.name()),
                () -> assertEquals(12, hero.strength()),
                () -> assertEquals("Легендарное", hero.rarity()),
                () -> assertEquals("Друиды", hero.heroClass()),
                () -> assertEquals("Зелёный", hero.element()),
                () -> assertTrue(hero.healer())
        );
    }
}