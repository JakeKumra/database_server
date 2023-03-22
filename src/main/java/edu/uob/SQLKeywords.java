package edu.uob;

import java.util.Set;
import java.util.HashSet;

public class SQLKeywords {
    private static final Set<String> keywords = new HashSet<>();
    static {
        keywords.add("SELECT");
        keywords.add("INSERT");
        keywords.add("UPDATE");
        keywords.add("DELETE");
        keywords.add("CREATE");
        keywords.add("ALTER");
        keywords.add("DROP");
        keywords.add("USE");
        keywords.add("ADD");
        keywords.add("DATABASE");
        keywords.add("FOREIGN");
        keywords.add("INNER");
        keywords.add("KEY");
        keywords.add("TABLE");
        keywords.add("NULL");
        keywords.add("NOT");
    }

    public static boolean isKeyword(String name) {
        return keywords.contains(name.toUpperCase());
    }
}

