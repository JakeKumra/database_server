package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UpdateCMD extends DBcmd {
    private String tableName;
    private String attributeName;
    private String newValue;
    private Condition condition;

    private boolean whereQuery;


    private List<SetClause> setClauseList;

    public UpdateCMD() {
        super();
        this.tableName = null;
        this.attributeName = null;
        this.newValue = null;
        this.condition = null;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isWhereQuery() {
        return whereQuery;
    }

    public void setWhereQuery(boolean whereQuery) {
        this.whereQuery = whereQuery;
    }

    public List<SetClause> getSetClauseList() {
        return setClauseList;
    }
    public void setSetClauseList(List<SetClause> setClauseList) {
        this.setClauseList = setClauseList;
    }


    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }
        List<SetClause> setClauseList = this.getSetClauseList();
        SetClause setClause = setClauseList.get(0);
        FileManager FM = new FileManager();
        setAttributeName(setClause.getAttributeName());
        try {
            if (s.getCurrentDatabase() == null) {
                return "[ERROR] no database has been selected";
            } else if (!s.getTableNames().contains(this.tableName)) {
                return "[ERROR] Table " + this.tableName + " does not exist in the database";
            }

            Table table = FM.parseFileToTable(tableName, s.getCurrDbName());

            // Update rows that match the condition
            int numRowsUpdated = table.updateRows(attributeName, setClause.getValue(), condition);
            String filePath = FM.getDbPath().toString() + File.separator + tableName;

            // only write the updated row to the file?
            FM.parseTableToFile(table, filePath);

            // Format the result string
            StringBuilder resultBuilder = new StringBuilder("[OK] ");
            resultBuilder.append(numRowsUpdated).append(" row");
            if (numRowsUpdated != 1) {
                resultBuilder.append("s");
            }
            resultBuilder.append(" updated");
            return resultBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] Failed to update data in database";
        }
    }
}
