package com.bantvegas.dietnyplan.util;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;

public class HtmlPlanFormatter {
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String formatToHtml(String markdown) {
        String[] lines = markdown.split("\n");
        StringBuilder updatedMarkdown = new StringBuilder();

        int dayCount = 0;
        int weekNumber = 1;

        updatedMarkdown.append("## Týždeň 1\n\n");

        for (String line : lines) {
            if (line.trim().matches("(?i)^###\\s*Deň\\s*\\d+.*")) {
                dayCount++;
                if (dayCount > 1 && (dayCount - 1) % 7 == 0) {
                    weekNumber++;
                    updatedMarkdown.append("\n---\n\n");
                    updatedMarkdown.append("## Týždeň ").append(weekNumber).append("\n\n");
                }
            }
            updatedMarkdown.append(line).append("\n");
        }

        return renderer.render(parser.parse(updatedMarkdown.toString()));
    }
}
