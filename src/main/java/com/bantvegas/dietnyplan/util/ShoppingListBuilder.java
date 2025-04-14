package com.bantvegas.dietnyplan.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoppingListBuilder {

    private static final Pattern INGREDIENT_PATTERN = Pattern.compile("(\\d+)(g|ml)\\s+([^,]+)");

    public static Map<String, Ingredient> extractShoppingList(String rawPlan) {
        Map<String, Ingredient> shoppingList = new LinkedHashMap<>();

        String[] lines = rawPlan.split("\r?\n");
        for (String line : lines) {
            line = line.trim();
            Matcher matcher = INGREDIENT_PATTERN.matcher(line);

            while (matcher.find()) {
                int amount = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                String name = matcher.group(3).toLowerCase();

                if (shoppingList.containsKey(name)) {
                    shoppingList.get(name).addAmount(amount);
                } else {
                    shoppingList.put(name, new Ingredient(name, unit, amount));
                }
            }
        }

        return shoppingList;
    }

    public static String toHtmlTable(Map<String, Ingredient> shoppingList) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Nákupný zoznam na 7 dní</h2>");
        html.append("<table border='1' cellspacing='0' cellpadding='6'>");
        html.append("<tr><th>Položka</th><th>Množstvo</th></tr>");
        for (Ingredient ing : shoppingList.values()) {
            html.append("<tr><td>").append(ing.name)
                    .append("</td><td>").append(ing.amount)
                    .append(" ").append(ing.unit).append("</td></tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    public static class Ingredient {
        String name;
        String unit;
        int amount;

        public Ingredient(String name, String unit, int amount) {
            this.name = name;
            this.unit = unit;
            this.amount = amount;
        }

        public void addAmount(int extra) {
            this.amount += extra;
        }
    }
}