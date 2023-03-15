package edu.uob;

import java.util.HashMap;

public class Database {

    private HashMap<String, Table> databaseTables;

    public Database () {
        this.databaseTables = new HashMap<>();
    }

    public HashMap<String, Table> getDbTables () {
        return databaseTables;
    }

    public void addTableToDb (String tableName, Table newTable) {
        databaseTables.put(tableName, newTable);
    }

}
