/*
 * Copyright (C) 2025 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package aigis.open2ch;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kiss.I;
import kiss.Storable;
import kiss.XML;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

public class OpenThread implements Storable<OpenThread> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy/MM/dd(EEE) HH:mm:ss", Locale.JAPANESE);

    public String title;

    public String url;

    public List<OpenComment> comments = new ArrayList();

    public Topics topics;

    /**
     * @param file
     */
    private void analyze(File htmlFile) {
        XML html = I.xml(htmlFile.asJavaPath());

        this.title = htmlFile.base();
        this.url = html.find("head meta[property='og:url']").attr("content");

        Directory log = I.env("log", Locator.directory(""));
        html.find("div.thread > dl").forEach(dl -> {
            int num = Integer.parseInt(dl.attr("val"));
            XML dt = dl.firstChild();
            XML name = dt.firstChild().next();
            String head = dl.firstChild().text();
            head = head.substring(head.indexOf('：', head.indexOf('：') + 1) + 1, head.indexOf("ID:")).trim();
            LocalDateTime date = LocalDateTime.parse(head, FORMATTER);
            String id = name.next().attr("val");

            XML dd = dt.next();
            dd.element("br").text("\r\n");
            String body = dd.text().trim().replaceAll("&gt;", ">");

            List<File> images = new ArrayList();
            dd.find("img").forEach(img -> images.add(log.file(img.attr("src"))));

            OpenComment comment = new OpenComment();
            comment.num = num;
            comment.id = id;
            comment.date = date;
            comment.name = name.text().replace("↓", "");
            comment.body = body;

            this.comments.add(comment);
        });
    }

    public OpenComment getCommentBy(int num) {
        return comments.get(num - 1);
    }

    public Topics getTopics() {
        if (topics == null) {
            topics = Editor.edit(this);
            store();
        }
        return topics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path locate() {
        return Path.of(".thread/" + title + ".json");
    }

    /**
     * @param file
     * @return
     */
    public static OpenThread of(File file) {
        String title = file.base();

        File cachedHTML = Locator.file(".thread/" + title + ".html");
        if (cachedHTML.isBefore(file)) {
            file.copyTo(cachedHTML);
            I.info("Cache original thread. [" + cachedHTML + "]");
        }

        OpenThread thread;
        File parsedJSON = Locator.file(".thread/" + title + ".json");
        if (parsedJSON.isBefore(cachedHTML)) {
            thread = new OpenThread();
            thread.analyze(file);
            thread.store();

            I.info("Parse thread. [" + parsedJSON + "]");
        } else {
            thread = new OpenThread();
            thread.title = title;
            thread.restore();

            I.info("Restore thread. [" + parsedJSON + "]");
        }

        if (thread.topics == null) {
            thread.topics = Editor.edit(thread);
            thread.store();

            I.info("Analyze topics. [" + thread.title + "]");
        }
        return null;
    }
}
