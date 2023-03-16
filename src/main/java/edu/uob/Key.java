package edu.uob;

public class Key {
    private String name;
    private Column column;
    private boolean isPrimaryKey;

    public Key(String name, Column column, boolean isPrimaryKey) {
        this.name = name;
        this.column = column;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public Column getColumn() {
        return column;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
}

