package org.example;

/**
 * Hero characteristics. Catalog-backed fields contain values from
 * {@code game-reference-data.json}.
 */
public record Hero(
        String name,
        int strength,
        String rarity,
        String heroClass,
        String element,
        boolean healer
) {
}