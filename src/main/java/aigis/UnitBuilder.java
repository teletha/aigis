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

import kiss.I;
import kiss.XML;

public class UnitBuilder {

    public static void main(String[] args) {
        Units units = I.make(Units.class);

        I.xml("https://aigis.fandom.com/wiki/Category:Player_Units?from=A").find(".category-page__member-link").forEach(link -> {
            String name = link.text();

            Unit unit = units.computeIfAbsent(name, key -> new Unit());
            unit.id = name;
            unit.path = link.attr("href");

            XML page = unit.page();
            String[] unitName = page.find(".unit-infobox .ui-text:nth-child(2)").text().strip().split("\\R");
            unit.nameJP = unitName[0].strip();
            unit.nameEN = unitName[2].strip();
            System.out.println(unit.nameJP + "  " + unit.nameEN);
        });

        units.store();
    }
}
