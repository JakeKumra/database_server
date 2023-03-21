package edu.uob;

import java.io.File;

public class DropCMD extends DBcmd {
    private String databaseName;
    private String tableName;

    private String errorMessage;

    private boolean parseError;

    public DropCMD() {
        this.parseError = false;
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
            FileManager FM = new FileManager();
            File databasesPath = new File (FM.getDbPath());
            File[] files = databasesPath.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals(databaseName)) {
                        // File exists, remove it
                        boolean deleted = file.delete();
                        if (deleted) {
                            if (s.getCurrDbName().equals(databaseName)) {
                                s.setCurrentDatabase(null);
                            }
                            return "[OK]" + " database " + databaseName + " has been removed";
                        } else {
                            return "[ERROR]" + " database " + databaseName + " hasn't been removed";
                        }
                    }
                }
            }
            return "[ERROR]" + " database " + databaseName + " can't be found";
        } else if (tableName != null) {
                // remove table file from database (which one)?
        } else {
            return "[ERROR]" + " Invalid DROP command.";
        }
        return "";
}}
