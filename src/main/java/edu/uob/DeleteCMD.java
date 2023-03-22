package edu.uob;

import java.io.File;
import java.io.IOException;

public class DeleteCMD extends DBcmd {
    private String tableName;
    private Condition condition;

    public DeleteCMD() {
        super();
        this.tableName = null;
        this.condition = null;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }

        try {
            if (s.getCurrentDatabase() == null) {
                return "[ERROR] no database has been selected";
            } else if (!s.getTableNames().contains(this.tableName)) {
                return "[ERROR] Table " + this.tableName + " does not exist in the database";
            }

            FileManager FM = new FileManager();
            Table table = FM.parseFileToTable(tableName, s.getCurrDbName());

            // Delete rows that match the condition (if present)
            int rowsDeleted = 0;
            try {
                rowsDeleted = table.deleteRows(this.condition);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return "[ERROR] invalid input query";
            }
            String filePath = FM.getDbPath() + File.separator + tableName;
            FM.parseTableToFile(table, filePath);

            // Format the result string
            String result = "[OK] " + rowsDeleted + " rows deleted";
            return result.trim();

        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] Failed to retrieve data from database";
        }
    }
}
