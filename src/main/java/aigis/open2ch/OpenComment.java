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

import java.time.LocalDateTime;
import java.util.List;

import psychopath.File;

public class OpenComment {
    public int num;

    public String name;

    public LocalDateTime date;

    public String id;

    public String body;

    public List<File> images;
}
