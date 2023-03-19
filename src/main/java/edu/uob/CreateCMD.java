package edu.uob;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class CreateCMD extends DBcmd {

    private String name;
    private List<String> attributes;

    private boolean isDatabaseCreation;

    public CreateCMD(String name, List<String> attributes, boolean isDatabaseCreation) {
        this.name = name;
        this.attributes = attributes;
        this.isDatabaseCreation = isDatabaseCreation;
    }

    public String query(DBServer s) {

        if (isDatabaseCreation == true) {
            File databaseDir = new File(new FileManager().getDatabasesPath() + File.separator + name);
            if (databaseDir.exists()) {
                return "[ERROR] Database " + name + " already exists.";
            } else if (!databaseDir.mkdir()) {
                return "[ERROR] Failed to create database directory.";
            } else {
                return "[OK] Database " + name + "created";
            }
        } else {
            if (attributes == null) {
                // create table with no attributes
                // create a file inside current database with name corresponding to name
                Database currDatabase = s.getCurrentDatabase();
                String currDatabaseName = currDatabase.getDatabaseName();
                // check to see that it's within the file system
                


            } else {
                // create table with attributes
            }


            return "Inside the Create table space";
        }


        // return "Response...";
    }
}
