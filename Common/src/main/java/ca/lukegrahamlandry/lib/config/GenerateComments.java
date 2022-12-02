/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: if the elements of an array, list or map have comment annotations, bring them up to the comment of that field
public class GenerateComments {
    /**
     * Converts an object to json. Any fields with the @Comment annotation will have a comment above them.
     * Gson does not support generating commented json5 (it will parse it tho). Since I don't want to bundle Jankson, I handle inserting comments myself.
     * @param instance the object to be converted to json
     * @param gson a gson instance with all required type adapters
     * @return a string with the json encoding of instance
     */
    public static <T> String commentedJson(T instance, Gson gson){
        return commentedJson(instance, gson.newBuilder().setPrettyPrinting().create(), 1);
    }

    private static <T> String commentedJson(T instance, Gson gson, int level){
        int comments = 0;
        for (Field field : instance.getClass().getFields()){
            Comment annotation = field.getAnnotation(Comment.class);
            if (annotation != null) comments++;
        }
        if (comments == 0) {
            StringBuilder output = new StringBuilder();
            List<String> lines = new ArrayList<>(Arrays.asList(gson.toJson(instance).split("\n")));
            output.append(lines.get(0));
            output.append("\n");
            lines.remove(0);

            for (String line : lines){
                output.append("  ".repeat(level - 1));
                output.append(line);
                output.append("\n");
            }

            output.deleteCharAt(output.length() - 1);

            return output.toString();
        }

        StringBuilder output = new StringBuilder();
        output.append("  ".repeat(level - 1));
        output.append("{\n");

        for (Field field : instance.getClass().getFields()){
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            Comment annotation = field.getAnnotation(Comment.class);
            if (annotation != null){
                String comment = annotation.value();
                output.append("  ".repeat(level));
                output.append("// ").append(comment).append("\n");
            }

            output.append("  ".repeat(level));
            output.append("\"").append(field.getName()).append("\": ").append(commentedJson(value, gson, level + 1)).append(",\n");
        }

        output.deleteCharAt(output.length() - 1);
        output.deleteCharAt(output.length() - 1);
        output.append("  ".repeat(level - 1));
        output.append("\n}\n");

        return output.toString();
    }
}
