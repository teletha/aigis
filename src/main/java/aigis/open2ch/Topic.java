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

import java.util.ArrayList;
import java.util.List;

import aigis.Markdown;
import kiss.I;
import psychopath.File;

public class Topic {

    public String title;

    public List<Integer> comments = new ArrayList();

    transient OpenThread thread;

    public void writeTo(File file) {
        StringBuilder text = new StringBuilder();
        for (int num : comments) {
            text.append(I.express("""
                    #### {num}: {name} {date} ID: {id}
                    {body}

                    """, thread.getCommentBy(Math.abs(num))));
        }

        Markdown.title(title).body(text.toString()).writeTo(file);

        I.info("Write article. [" + file + "]");
    }
}
