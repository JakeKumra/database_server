package edu.uob;
import java.util.ArrayList;

public class UseCMD extends DBcmd {
    private String dbName;
    public UseCMD(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String query(DBServer s) {
        // Check if the database exists
        if (s.getDatabase(dbName) == null) {
            return "ERROR: Database " + dbName + " does not exist";
        }

        // Set the current database
        s.setCurrentDb(dbName);
        return "Database changed to " + dbName;
    }
}

