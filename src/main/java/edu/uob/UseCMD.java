package edu.uob;
import java.util.ArrayList;

public class UseCMD extends DBcmd {
    private String dbName;
    public UseCMD(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String query(DBServer s) {
        Database dbFromFile = s.getDatabaseFromFile(dbName);
        // Check if the database exists
        if (dbFromFile == null) {
            return "[ERROR] Database " + dbName + " does not exist";
        }

        System.out.println(dbFromFile.getDatabaseName());
        // Set the current database
        s.setCurrentDatabase(dbFromFile);
        return "[OK] Database changed to " + dbName;
    }
}

