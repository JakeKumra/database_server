package edu.uob;

import java.io.File;
import java.io.IOException;

import static edu.uob.SQLKeywords.isKeyword;

public class AlterCMD extends DBcmd {

    private String tableName;

    private String alterationType;

    private String attributeName;

    public AlterCMD (){
        super();
        this.tableName = null;
        this.alterationType = null;
        this.attributeName = null;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public void setAlterationType(String alterationType) {
        this.alterationType = alterationType;
    }
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String query(DBServer s) {

        if (parseError) {
            return errorMessage;
        } else if (s.getCurrentDatabase() == null) {
            return "[ERROR] no database has been selected";
        } else if (!s.getTableNames().contains(this.tableName)) {
            return "[ERROR] Table " + this.tableName + " does not exist in the database";
        } else if (isKeyword(tableName)) {
            return "[ERROR] reserved SQL keyword used";
        }
        try {
            FileManager FM = new FileManager();
            Table table = FM.parseFileToTable(tableName, s.getCurrDbName());
            if (alterationType.equalsIgnoreCase("DROP")) {
                // check that it's not the id column
                if (attributeName.equalsIgnoreCase("id")) {
                    return "[ERROR] id column cannot be removed";
                }
                // check that the attribute name is within the table
                String[] headers = table.getHeaders();
                boolean tableFound = false;
                for (String header : headers) {
                    if (header.equals(attributeName)) {
                        tableFound = true;
                    }
                }
                if (!tableFound) {
                    return "[ERROR] attribute type " + attributeName + " not found in table";
                }
                int index = table.getHeaderIndex(attributeName);
                if (index != -1) {
                    table.removeColumn(index, attributeName);
                } else {
                    return "[ERROR] attribute type " + attributeName + " not found in table";
                }
                FileManager FM2 = new FileManager();
                String filePath = FM2.getDbPath() + File.separator + s.getCurrDbName() + File.separator + tableName;
                FM2.parseTableToFile(table, filePath);
                return "[OK] table column " + attributeName + " dropped";
            } else if (alterationType.equalsIgnoreCase("ADD")) {
                String[] headers = table.getHeaders();
                for (String header : headers) {
                    if (header.equals(attributeName)) {
                        return "[ERROR] attribute type " + attributeName + " already in table";
                    }
                }
                return "[OK] table column " + attributeName + " was added";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] unable to load table " + this.tableName + " from database";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "[ERROR] an error has occurred";
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return "[ERROR] an error has occurred";
        }
        return "[ERROR] an error has occurred";
    }
}

