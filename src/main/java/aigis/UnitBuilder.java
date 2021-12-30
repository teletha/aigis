/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package aigis;

import java.util.Set;
import java.util.stream.IntStream;

import kiss.I;

public class UnitBuilder {

    public static void main(String[] args) {
        Units units = I.make(Units.class);

        I.signal(units.values()).take(1).to(unit -> {
            unit.analyze();
        });

        units.store();
    }

    public static void collectAllUnit() {
        Units units = I.make(Units.class);

        Set<String> excludes = Set
                .of("Aina", "Alegria", "Amour", "Black Armor", "Bonbori", "Christia", "Delight", "Elizabeth of Blessings", "Farah", "Fes", "Fleur", //
                        "Freude", "Hanny", "Heroic Naiad", "Iyo", "Imperial Platinum Armor", "Joy", "Liebe", "Licht", "Mizuchi", "Musica", "Naiad", //
                        "Onyx", "Placer", "Praise", "Plaisir", "Platinum Armor", "Rire", "Sara", "Sariette", "Senko", "Seremo", "Sol", "Tale", "Tsubasa", "Victoire");

        IntStream.rangeClosed('A', 'Z').forEach(c -> {
            I.xml("https://aigis.fandom.com/wiki/Category:Player_Units?from=" + (char) c)
                    .find(".category-page__member-link")
                    .forEach(link -> {
                        String name = link.text();

                        // exclude spirits
                        if (excludes.contains(name) || name.startsWith("Gladys") || name.startsWith("Happy") || name
                                .startsWith("Nina") || name.startsWith("Life-Sized") || name.startsWith("Trene") || name
                                        .startsWith("Celia") || name.startsWith("Cyrille") || name.startsWith("Florika")) {
                            return;
                        }

                        // ecludes alternative unit
                        if (name.endsWith("of the Foreign Land")) {
                            return;
                        }

                        Unit unit = units.computeIfAbsent(name, key -> new Unit());
                        unit.id = name;

                        unit.analyze();
                    });
        });

        units.store();
    }
}
