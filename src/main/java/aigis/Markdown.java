/*
 * Copyright (C) 2025 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package aigis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kiss.I;
import kiss.Managed;
import psychopath.File;

public class Markdown {

    @Managed
    private String title;

    @Managed
    private LocalDateTime published = LocalDateTime.now();

    @Managed
    private List<String> tags = new ArrayList();

    @Managed
    private String category = "";

    @Managed
    private String body;

    public Markdown body(String body) {
        this.body = Objects.requireNonNull(body);
        return this;
    }

    public void writeTo(File file) {
        file.text(I.express("""
                ---
                title: {title}
                published: {published}
                tags: {tags}
                category: {category}
                ---
                {body}
                """, this));
    }

    public static Markdown title(String title) {
        Markdown md = new Markdown();
        md.title = Objects.requireNonNull(title);
        return md;
    }
}
