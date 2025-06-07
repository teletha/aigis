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

import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import kiss.I;

public class Editor {

    private static final GoogleAiGeminiChatModel MODEL = GoogleAiGeminiChatModel.builder()
            .apiKey(I.env("GeminiAPIKey"))
            .modelName("gemini-2.5-flash-preview-05-20")
            .responseFormat(ResponseFormat.JSON)
            .build();

    public static Topics edit(OpenThread thread) {
        StringBuilder text = new StringBuilder();
        for (OpenComment comment : thread.comments) {
            text.append("#").append(comment.num).append("\n");
            text.append(comment.body).append("\n\n");
        }

        I.info("Analyze [" + thread.title + "] by Gemini.");

        String json = AiServices.create(Editing.class, MODEL).selectTopics(text.toString());
        return I.json(json, Topics.class);
    }

    interface Editing {
        @SystemMessage("""
                あなたはプロの編集者です、まとめサイトを作成するために提供されたスレッド全文の中から
                話題になっている全てのトピックを挙げてください。（できれば5個以上）

                各トピックに対してキャッチーなタイトルをつけてください。
                そのトピックに関連するコメントは全てコメント番号を関連付けてください、なおコメントの順番は番号順でなくてもよく、意味がわかりやすくなるように入れ替えてもいいです。オチとして使えそうなコメントであれば最後に持ってくること。
                ひとつのトピックに関連づけるコメントは40個を絶対に超えないでください、超えてしまう場合は余計なものを間引いてください。
                強調すべきコメントがあればその番号は負数にしてください。

                JSON形式で情報を返してください。
                [
                    {
                        "title": "キャッチーなタイトル",
                        "comments": [-1, 2, 5, 10, 22, -13]
                    },
                    {
                        "title": "キャッチーなタイトル",
                        "comments": [300, 302, 321, 311, 312, -325]
                    }
                ]
                """)
        String selectTopics(String text);
    }
}
