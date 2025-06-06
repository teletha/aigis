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

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import kiss.I;

public class Editor {

    private static final GoogleAiGeminiChatModel MODEL = GoogleAiGeminiChatModel.builder()
            .apiKey(I.env("GeminiAPIKey"))
            .modelName("gemini-2.5-flash-preview-05-20")
            .build();

    public static Topics edit(OpenThread thread) {
        StringBuilder text = new StringBuilder();
        for (OpenComment comment : thread.comments) {
            text.append("#").append(comment.num()).append("\n");
            text.append(comment.body()).append("\n\n");
        }

        return AiServices.create(Editing.class, MODEL).selectTopics(text.toString());
    }

    interface Editing {
        @SystemMessage("""
                あなたはプロの編集者です、まとめサイトを作成するために提供されたスレッド全文の中から
                話題になっている全てのトピックを挙げてください。（できれば5個以上）

                各トピックに対してキャッチーなタイトルをつけてください。
                そのトピックに関連するコメントは全てコメント番号を関連付けてください、なおコメントの順番は番号順でなくてもよく、意味がわかりやすくなるように入れ替えてもいいです。オチとして使えそうなコメントであれば最後に持ってくること。
                ひとつのトピックに関連づけるコメントは40個を絶対に超えないでください、超えてしまう場合は余計なものを間引いてください。
                強調すべきコメントがあればその番号を関連付けてください。
                """)
        Topics selectTopics(String text);
    }
}
