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

import aigis.open2ch.OpenThread;
import kiss.I;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

public class Open2ch {

    public static void main(String[] args) {
        Directory log = I.env("log", Locator.directory(""));
        log.walkDirectory("*_files").to(dir -> {
            String title = dir.name();
            title = title.substring(0, title.length() - 6);
            File file = log.file(title + ".htm");
            if (file.isPresent()) {
                OpenThread thread = OpenThread.of(file);

                // for (Topic topic : thread.getTopics().articles) {
                // System.out.println("######################################");
                // System.out.println(topic.title() + " " + topic.commnets().size());
                // System.out.println("######################################");
                // for (Integer id : topic.commnets()) {
                // if (topic.pickups().contains(id)) {
                // System.out.println("@ " + id + "\r\n" + thread.getCommentBy(id).body());
                // } else {
                // System.out.println(id + "\r\n" + thread.getCommentBy(id).body());
                // }
                // }
                //
                // System.out.println("");
                // }
            }
        });
    }
}
