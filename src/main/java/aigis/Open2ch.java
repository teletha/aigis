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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aigis.open2ch.OpenComment;
import aigis.open2ch.OpenThread;
import kiss.I;
import kiss.XML;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

public class Open2ch {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy/MM/dd(EEE) HH:mm:ss", Locale.JAPANESE);

    public static void main(String[] args) {
        Directory log = I.env("log", Locator.directory(""));
        log.walkDirectory("*_files").to(dir -> {
            String title = dir.name();
            title = title.substring(0, title.length() - 6);
            File file = log.file(title + ".htm");
            if (file.isPresent()) {
                XML html = I.xml(file.asJavaPath());
                OpenThread thread = new OpenThread(title, html.find("head meta[property='og:url']").attr("content"));

                List<OpenComment> comments = new ArrayList();
                html.find("div.thread > dl").forEach(dl -> {
                    int num = Integer.parseInt(dl.attr("val"));
                    String name = dl.firstChild().firstChild().next().text();
                    // String head = dl.firstChild().text();
                    // head = head.substring(head.indexOf('：', head.indexOf('：') + 1) + 1,
                    // head.indexOf("ID:")).trim();
                    // LocalDateTime date = LocalDateTime.parse(head, FORMATTER);
                    // String id = dl.find(".id").text();

                    // XML dd = dl.find("dd");
                    // dd.find("br").text("\r\n");
                    // String body = dd.text().trim().replaceAll("&gt;", ">");

                    comments.add(new OpenComment(num, name, null, "", ""));
                    System.out.println(comments.getLast());
                });

                // System.out.println(comments);

                // String body = xml.toString();
                //
                // GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                // .apiKey("AIzaSyB6C9O9bMJM-pAlp3hDo6emgx3jFq501kI")
                // .modelName("gemini-2.5-flash-preview-05-20")
                // .build();
                //
                // String chat = model.chat(body + "\r\n" + """
                // あなたはプロの編集者です、まとめサイトを作成するために上記に提示されたスレッドの中から
                // 話題になっている全てのトピックを挙げてください。（できれば7個以上）
                // 各トピックに対してキャッチーなタイトルをつけて。
                // そのトピックに関連するコメントは全文そのまま表示して。
                // 各トピックの中で核となるコメントがあれば強調して。
                // 新登場のユニットに関する話題は必ず取り上げてください、ただし複数のユニットが被らないように、それぞれ別のトピックとして扱ってね。
                //
                // 表示する際のフォーマットは
                //
                // ## キャッチーなタイトル
                // - 番号 *コメント全文* （発端となる発言は強調）
                // - 番号 コメント全文
                // - 番号 コメント全文
                // - 番号 （オチとなるような）コメント全文
                //
                // てな感じで頼みます
                // """);
                // System.out.println(chat);
            }
        });
    }
}
