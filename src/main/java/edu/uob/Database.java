package edu.uob;

import java.util.HashMap;

public class Database {
    private HashMap<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void addTable(String tableName, Table table) {
        tables.put(tableName, table);
    }

    public void removeTable(String tableName) {
        tables.remove(tableName);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }
}
