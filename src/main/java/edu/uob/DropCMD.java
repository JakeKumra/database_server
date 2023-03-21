package edu.uob;

import java.io.File;

public class DropCMD extends DBcmd {

    private String databaseName;
    private String tableName;

    public DropCMD() {
        super();
        this.databaseName = null;
        this.tableName = null;
    }

    public void setParseError() {
        this.parseError = true;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }
        if (databaseName != null) {
            File dbFile = new File(new FileManager().getDbPath(), databaseName);
            if (dbFile.exists() && dbFile.delete()) {
                if (s.getCurrDbName().equals(databaseName)) {
                    s.setCurrentDatabase(null);
                }
                return "[OK] database " + databaseName + " has been removed";
            } else {
                return "[ERROR] database " + databaseName + " can't be found or removed";
            }
        } else if (tableName != null) {
            Database currDb = s.getCurrentDatabase();
            if (currDb != null) {
                File tableFile = new File(new FileManager().getDbPath() + File.separator + s.getCurrDbName(), tableName);
                if (tableFile.exists() && tableFile.delete()) {
                    return "[OK] " + tableName + " has been deleted from database";
                } else {
                    return "[ERROR] table " + tableName + " not found in database / can't be removed";
                }
            } else {
                return "[ERROR] no database has been selected";
            }
        } else {
            return "[ERROR] Invalid DROP command.";
        }
    }
}
