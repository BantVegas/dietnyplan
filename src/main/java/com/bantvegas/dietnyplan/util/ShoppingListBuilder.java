package com.bantvegas.dietnyplan.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoppingListBuilder {

    // Extrahuje potraviny bez ohľadu na množstvo
    private static final Pattern ITEM_PATTERN = Pattern.compile("(?i)(?:raňajky|desiata|obed|olovrant|večera):\\s*(.+)");

    public static Map<String, Ingredient> extractShoppingList(String rawPlan) {
        Map<String, Ingredient> shoppingList = new LinkedHashMap<>();
        String[] lines = rawPlan.split("\r?\n");

        for (String line : lines) {
            Matcher matcher = ITEM_PATTERN.matcher(line);
            if (matcher.find()) {
                String meals = matcher.group(1).toLowerCase();

                // rozdelíme podľa čiarky a AND slov (približne)
                String[] items = meals.split("[,\\+]| a | s ");
                for (String item : items) {
                    item = item.replaceAll("[^a-zA-Zá-žÁ-Žč-žČ-Ž\\s]", "").trim();
                    if (item.length() > 2 && !shoppingList.containsKey(item)) {
                        shoppingList.put(item, new Ingredient(item));
                    }
                }
            }
        }

        return shoppingList;
    }

    public static String toHtmlTable(Map<String, Ingredient> shoppingList) {
        if (shoppingList.isEmpty()) return "";

        StringBuilder html = new StringBuilder();
        html.append("<h2>Nákupný zoznam na 7 dní</h2>");
        html.append("<table>");
        html.append("<tr><th>Položka</th></tr>");
        for (Ingredient ing : shoppingList.values()) {
            html.append("<tr><td>").append(ing.name).append("</td></tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    public static class Ingredient {
        String name;

        public Ingredient(String name) {
            this.name = name;
        }
    }
}
