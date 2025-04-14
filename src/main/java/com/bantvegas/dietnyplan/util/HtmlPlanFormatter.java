package com.bantvegas.dietnyplan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlPlanFormatter {

    private static final Pattern KCAL_PATTERN = Pattern.compile("\\((\\d+)\\s*kcal\\)");

    public static String formatToHtml(String rawPlan) {
        StringBuilder html = new StringBuilder();
        String[] lines = rawPlan.split("\\r?\\n");

        String currentDay = null;
        String breakfast = "", snack1 = "", lunch = "", snack2 = "", dinner = "";
        int dailyCalories = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.matches("(?i)^deň \\d+:?")) {
                if (currentDay != null) {
                    html.append(renderDay(currentDay, breakfast, snack1, lunch, snack2, dinner, dailyCalories));
                }
                currentDay = line;
                breakfast = snack1 = lunch = snack2 = dinner = "";
                dailyCalories = 0;
            } else if (line.toLowerCase().startsWith("raňajky:")) {
                breakfast = line.substring(9).trim();
                dailyCalories += extractCalories(breakfast);
            } else if (line.toLowerCase().startsWith("desiata:")) {
                snack1 = line.substring(8).trim();
                dailyCalories += extractCalories(snack1);
            } else if (line.toLowerCase().startsWith("obed:")) {
                lunch = line.substring(5).trim();
                dailyCalories += extractCalories(lunch);
            } else if (line.toLowerCase().startsWith("olovrant:")) {
                snack2 = line.substring(9).trim();
                dailyCalories += extractCalories(snack2);
            } else if (line.toLowerCase().startsWith("večera:")) {
                dinner = line.substring(8).trim();
                dailyCalories += extractCalories(dinner);
            }
        }

        if (currentDay != null) {
            html.append(renderDay(currentDay, breakfast, snack1, lunch, snack2, dinner, dailyCalories));
        }

        return html.toString();
    }

    private static int extractCalories(String text) {
        Matcher matcher = KCAL_PATTERN.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private static String renderDay(String day, String breakfast, String snack1, String lunch, String snack2, String dinner, int kcal) {
        return String.format("""
            <div class=\"day\">%s</div>
            <table>
                <tr><th>Raňajky</th><td>%s</td></tr>
                <tr><th>Desiata</th><td>%s</td></tr>
                <tr><th>Obed</th><td>%s</td></tr>
                <tr><th>Olovrant</th><td>%s</td></tr>
                <tr><th>Večera</th><td>%s</td></tr>
                <tr><th><b>Celkový denný príjem</b></th><td><b>%d kcal</b></td></tr>
            </table>
        """, day, breakfast, snack1, lunch, snack2, dinner, kcal);
    }
}