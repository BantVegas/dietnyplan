package com.bantvegas.dietnyplan.util;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;

public class HtmlPlanFormatter {
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String formatToHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}
