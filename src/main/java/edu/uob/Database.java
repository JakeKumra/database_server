package edu.uob;

import java.util.HashMap;

public class Database {
    private HashMap<String, oldTableToBeDeletedLater> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void addTable(String tableName, oldTableToBeDeletedLater table) {
        tables.put(tableName, table);
    }

    public void removeTable(String tableName) {
        tables.remove(tableName);
    }

    public oldTableToBeDeletedLater getTable(String tableName) {
        return tables.get(tableName);
    }
}
