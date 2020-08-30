package main;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String ID;
    private static String confDir;
    private static Properties properties;

    public static void main(String[] args) throws IOException {

        // Parsing props file
        if (args.length < 1) {
            System.out.println("Нет параметров");
            return;
        }
        parseData(args[0]);

        String text = Files.readString(Path.of(confDir));
        String regexSection = getSectionByID(text);
        if (regexSection.equals("")) {
            System.out.println("Нет секции с указаным ID: " + ID);
            return;
        }

        // More than one line
        boolean isSuccessful = false;
        System.out.println(regexSection);
        for (String regexLine:
                properties.stringPropertyNames()) {

            // Remove quotation marks
            String regex = properties.getProperty(regexLine).replace("\"", "");
            if (findLine(regexSection, regex))
                isSuccessful = true;
            else
                System.out.println("Не удалось найти запись: " + regexLine);
        }
        if (isSuccessful) {
            System.out.println("Успешное завершение");
        }
    }

    private static void parseData(String path) {
        File file = new File(path);
        properties = new Properties();
        try {
            properties.load(new FileReader(file));

            // Get ID
            ID = properties.getProperty("id");
            properties.remove("id");

            // Get path
            confDir = properties.getProperty("path");
            properties.remove("path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSectionByID(String text) {
        String regexSection = String.format("\\[id\\]\\nid= %s(.|\\b\\n)*", ID);
        Pattern pattern = Pattern.compile(regexSection);
        Matcher matcher = pattern.matcher(text);

        // Get first found section
        String section = "";
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        } else {
            return "";
        }
    }

    private static boolean findLine(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.find();
    }
}
