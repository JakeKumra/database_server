package edu.uob;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

public class CreateCMD extends DBcmd {
    private String name;
    private List<String> attributes;
    private boolean isDatabaseCreation;
    public CreateCMD(String name, List<String> attributes, boolean isDatabaseCreation) {
        super();
        this.name = name;
        this.attributes = attributes;
        this.isDatabaseCreation = isDatabaseCreation;
    }

    boolean duplicateAttFound() {
        if (attributes == null) {
            return false;
        }
        for (int i=0; i<attributes.size(); i++) {
            for (int j=i+1; j<attributes.size(); j++) {
                if (attributes.get(i).equals(attributes.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        } else if (reservedKeywordFound(name, attributes)) {
            return "[ERROR] Attempt to use reserved keyword";
        }
        try {
            if (isDatabaseCreation) {
                return createDatabase(name);
            } else {
                // creating a new table within current database
                String currDatabaseName = s.getCurrDbName();
                String path = new FileManager().getDbPath() + File.separator + currDatabaseName;
                File tableFile = new File (path + File.separator + name);
                if (tableFile.exists()) {
                    return "[ERROR]" + " table " + name + " already exists within database" + currDatabaseName;
                } else if (duplicateAttFound()) {
                    return "[ERROR]" + " table " + name + " contains duplicate attributes";
                }
                if (attributes == null) {
                    if (tableFile.exists()) {
                        tableFile.createNewFile();
                    }
                } else {
                    tableFile.createNewFile();
                    // write attributes to the first line of the table file
                    FileWriter writer = new FileWriter(tableFile);
                    writer.write("id\t");
                    for (String attribute : attributes) {
                        writer.write(attribute + "\t");
                    }
                    writer.write("\n");
                    writer.close();
                }
                return "[OK] TABLE " + name + " created";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] has occurred";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "[ERROR] has occurred";
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "[ERROR] has occurred";
        }
    }

    private String createDatabase (String name) {
        File databaseDir = new File(new FileManager().getDbPath() + File.separator + name);
        if (databaseDir.exists()) {
            return "[ERROR] Database " + name + " already exists.";
        } else if (!databaseDir.mkdir()) {
            return "[ERROR] Failed to create database directory.";
        } else {
            return "[OK] Database " + name + " created";
        }
    }

    public boolean reservedKeywordFound (String name, List<String> attributes) {
        if (SQLKeywords.isKeyword(name)) {
             return true;
        }
        if (attributes != null) {
            for (String attribute : attributes) {
                if (SQLKeywords.isKeyword(attribute)) {
                    return true;
                }
            }
        }
        return false;
    }
}
