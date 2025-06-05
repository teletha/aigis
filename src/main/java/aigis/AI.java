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

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class AI {

    public static void main(String[] args) {
        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey("AIzaSyB6C9O9bMJM-pAlp3hDo6emgx3jFq501kI")
                .modelName("gemma-3-27b-it")
                .build();

        String chat = model.chat("英語で「こんにちは」って言って");
        System.out.println(chat);
    }
}
