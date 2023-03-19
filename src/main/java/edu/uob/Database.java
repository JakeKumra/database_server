package edu.uob;

import java.util.HashMap;

public class Database {

    private HashMap<String, Table> tables;

    private String databaseName;

    public Database(String name) {
        this.tables = new HashMap<>();
        this.databaseName = name;
    }

    public String getDatabaseName() {
        return databaseName;
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

    public HashMap<String, Table> getAllTables() {
        return tables;
    }

}
