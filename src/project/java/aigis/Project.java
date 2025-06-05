package aigis;

/*
 * Copyright (C) 2021 The AIGIS Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
import static bee.api.License.*;

public class Project extends bee.api.Project {
    {
        product("com.github.teletha", "aigis", "0.1");
        license(MIT);

        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "psychopath");
        require("com.atilika.kuromoji", "kuromoji-ipadic");
        require("dev.langchain4j", "langchain4j");
        require("dev.langchain4j", "langchain4j-google-ai-gemini");

        versionControlSystem("https://github.com/teletha/aigis");
    }
}