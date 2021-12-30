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

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.atilika.kuromoji.ipadic.Tokenizer;

import kiss.I;
import kiss.XML;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

public class Unit {

    private static final Directory UnitRoot = Locator.directory(".data/unit");

    private static final Tokenizer tokenizer;

    static {
        try {
            tokenizer = new Tokenizer.Builder().userDictionary("src/main/resources/aigis/kuromoji.csv").build();
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }

    public String id;

    public String name;

    public String roman;

    public String reading;

    public String body;

    public String artist;

    public String artistLink;

    public int rare;

    public String clazz;

    public String skill;

    public String skillAwake;

    public String ability;

    /**
     * Read cached page data.
     * 
     * @return
     */
    public XML page() {
        // try to read from local
        File localPage = UnitRoot.directory(id).file("page.html");

        if (localPage.isAbsent()) {
            I.info("Download page for [" + id + "]");

            // try to read from network
            XML xml = I.xml("https://aigis.fandom.com/wiki/" + id.replace(' ', '_').replace("&#039;", "'"));

            // remove all script and svg elements
            xml.find("script, svg").remove();

            // save as cache
            xml.to(localPage.newBufferedWriter());
        }

        return I.xml(localPage.asJavaPath());
    }

    /**
     * Read cached page data.
     * 
     * @return
     */
    public XML pageJP() {
        // try to read from local
        File localPage = UnitRoot.directory(id).file("page-jp.html");

        if (localPage.isAbsent()) {
            I.info("Download page for [" + name + "]");

            // try to read from network
            XML xml = I.xml("https://wikiwiki.jp/aigiszuki/" + name.replace(" ", "%20"));

            // remove all script and svg elements
            xml.find("script, svg").remove();

            // save as cache
            xml.to(localPage.newBufferedWriter());
        }

        return I.xml(localPage.asJavaPath());
    }

    /**
     * Analyze unit info from page.
     */
    void analyze() {
        XML page = page();
        analyzeName(page);
        analyzeMeasurement(page);
        analyzeArtist(page);
        analyzeRarity(page);

        if (0 <= rare) {
            page = pageJP();
            analyzeClass(page);
            analyzeSkill(page);
        }
    }

    /**
     * Analyze name related info.
     * 
     * @param page
     */
    private void analyzeName(XML page) {
        String[] names = page.find(".unit-infobox .ui-text:nth-child(2)").text().strip().split("\\R");
        if (names.length == 1) {
            name = names[0].strip();
        } else if (2 <= names.length) {
            name = names[0].strip();
            roman = names[1].strip().replace("-", "");
        } else if (3 <= names.length) {
            name = names[0].strip();
            roman = names[2].strip().replace("-", "");
        }

        String katakana = tokenizer.tokenize(name)
                .stream()
                .map(token -> token.getReading().equals("*") ? token.getSurface() : token.getReading())
                .collect(Collectors.joining());
        String hiragana = toHiragana(katakana);

        reading = hiragana + " " + katakana;
    }

    private String toHiragana(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
            case 'ー':
            case '【':
            case '】':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
                builder.append(c);
                break;

            case 'ヴ':
                builder.append("ぶ");
                break;

            case ' ':
                break;

            default:
                builder.append((char) (c - 96));
                break;
            }
        }
        return builder.toString();
    }

    private static final Pattern MeasurementFormat = Pattern.compile("(\\d+)cm\\s+/\\s+B:(\\d+)\\s+W:(\\d+)\\s+H:(\\d+)");

    /**
     * Analyze measurement related info.
     * 
     * @param page
     */
    private void analyzeMeasurement(XML page) {
        String data = page.find(".unit-infobox > .ui-databox > .ui-data").text();
        Matcher matcher = MeasurementFormat.matcher(data);
        if (matcher.find()) {
            StringJoiner join = new StringJoiner(" ", "", "");
            join.add(matcher.group(1));
            join.add(matcher.group(2));
            join.add(matcher.group(3));
            join.add(matcher.group(4));
            body = join.toString();
        } else {
            body = "";
        }
    }

    /**
     * Analyze measurement related info.
     * 
     * @param page
     */
    private void analyzeArtist(XML page) {
        XML xml = page.find(".unit-infobox > .ui-databox.InfoboxBase > .ui-data");
        artist = xml.text().replace("(Undisclosed)", "").replace("Undisclosed", "非公開").replaceAll("\\R", "").replaceAll("\\s", "").strip();
        artistLink = xml.find("a").attr("href");
    }

    /**
     * Analyze rarity related info.
     * 
     * @param page
     */
    private void analyzeRarity(XML page) {
        String data = page.find("a[title^=\"Category:Rarity\"]").first().text().strip().toLowerCase();
        int index = data.indexOf(':');
        if (index != -1) {
            data = data.substring(index + 1).strip();
        }

        this.rare = switch (data) {
        case "iron" -> 0;
        case "bronze" -> 1;
        case "silver" -> 2;
        case "gold" -> 3;
        case "platinum" -> 4;
        case "sapphire" -> 5;
        case "black" -> 6;
        case "unique" -> -1;
        default ->
            // If this exception will be thrown, it is bug of this program. So we must
            // rethrow the wrapped error in here.
            throw new Error(id + "  " + data);
        };
    }

    /**
     * Analyze class related info.
     * 
     * @param page
     */
    private void analyzeClass(XML page) {
        StringJoiner joiner = new StringJoiner(" ");
        XML columns = page.find("#content > div.h-scrollable").first().find("tbody tr:nth-child(odd)");
        for (XML column : columns) {
            XML target = joiner.length() == 0 ? column.firstChild().next() : column.firstChild();
            joiner.add(text(target));
        }
        clazz = joiner.toString();
    }

    /**
     * Analyze skill related info.
     * 
     * @param page
     */
    private void analyzeSkill(XML page) {
        XML node = page.find("a[title=\"スキル覚醒\"]").first().parent().next().next().find("tbody > tr:first-child > td:first-child");
        if (text(node).equals("スキル")) {
            node = node.parent().next().firstChild();
        }

        StringJoiner awaked = new StringJoiner(" ");
        awaked.add(text(node.next()));
        String span = node.attr("rowspan");
        node = node.parent();
        for (int i = span.isEmpty() ? 0 : Integer.parseInt(span) - 1; 0 < i; i--) {
            node = node.next();
            awaked.add(text(node.firstChild()));
        }

        StringJoiner normal = new StringJoiner(" ");
        node = node.next().firstChild();
        normal.add(text(node.next()));
        span = node.attr("rowspan");
        node = node.parent();
        for (int i = span.isEmpty() ? 0 : Integer.parseInt(span) - 1; 0 < i; i--) {
            node = node.next();
            normal.add(text(node.firstChild()));
        }
        skill = normal.toString();
    }

    /**
     * Get the normalized text.
     * 
     * @param xml
     * @return
     */
    private String text(XML xml) {
        return xml.text().replaceAll("\\s", "");
    }
}
