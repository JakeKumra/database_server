package edu.uob;

import java.util.HashMap;
import java.util.ArrayList;

public class Table {

    // can be String and Object instead of String and String
    private ArrayList<HashMap<String, String>> tableInstance;

    public Table() {
        this.tableInstance = new ArrayList<>();
    }

    public ArrayList<HashMap<String, String>>  getAllTableData () {
        return tableInstance;
    }
    public void addRow (HashMap<String, String> newRow) {
        try {
            checkDuplicatePK(newRow);
            // checkMissingColumns(newRow);
            this.tableInstance.add(newRow);
        } catch (TableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // TODO test that this function is working correctly
    public void checkDuplicatePK(HashMap<String, String> rowToCheck) throws TableException {
        Object newRowPKString = rowToCheck.get("id");
        for (HashMap<String, String> eachRow : tableInstance) {
            // compare the primary key row in eachRow to the rowToCheck and make sure there is no duplicate found
            String value = eachRow.get("id");
            if (value != null && value.equals(newRowPKString)) {
                throw new TableException("Duplicate primary key found in row.");
            }
        }
    }

    public ArrayList<String> getColumnData(String columnName) {
        ArrayList<String> tableColumn = new ArrayList<>();
        for (int i=0; i<tableInstance.size(); i++) {
            tableColumn.add(tableInstance.get(i).get(columnName));
        }
        return tableColumn;
    }

    public HashMap<String, String> getRowData (int rowNum) {
        return tableInstance.get(rowNum);
    }



    // TODO implement the functionality for this method
//    public void checkMissingColumns(HashMap<String, String> rowToCheck) throws TableException {
//        // if missing columns throw error, else return true
//        throw new TableException("Missing columns in row.");
//    }
}
