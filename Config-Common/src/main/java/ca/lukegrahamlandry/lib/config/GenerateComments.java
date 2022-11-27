package ca.lukegrahamlandry.lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: if the elements of an array, list or map have comment annotations, bring them up to the comment of that field
public class GenerateComments {
    public static <T> String commentedJson(T instance, Gson gson){
        return commentedJson(instance, gson.newBuilder().setPrettyPrinting().create(), 1);
    }

    public static <T> String commentedJson(T instance, Gson gson, int level){
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
