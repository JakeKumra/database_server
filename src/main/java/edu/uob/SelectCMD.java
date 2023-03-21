package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectCMD extends DBcmd {
    private List<String> attributeList;
    private boolean whereQuery;
    private String tableName;
    private Condition condition;

    public SelectCMD() {
        super();
        this.attributeList = new ArrayList<>();
        this.tableName = null;
        this.whereQuery = false;
        this.condition = null;
        this.parseError = false;
    }

    public void setWhereQuery(boolean bool) {
        this.whereQuery = bool;
    }

    public boolean getWhereQueryStatus() {
        return this.whereQuery;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public void addAttribute(String attributeName) {
        this.attributeList.add(attributeName);
    }

    public void removeAttribute(String attributeName) {
        this.attributeList.remove(attributeName);
    }

    public boolean hasWildcard() {
        return this.attributeList.size() == 1 && this.attributeList.get(0).equals("*");
    }

    public boolean hasAttribute(String attributeName) {
        return this.attributeList.contains(attributeName);
    }

    // TODO maybe try to split this function up as it's too long
    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }

        try {
            if (s.getCurrentDatabase() == null) {
                return "[ERROR] no database has been selected";
            }
            if (!s.getTableNames().contains(this.tableName)) {
                return "[ERROR] Table " + this.tableName + " does not exist in the database";
            }
            Table table = s.parseFileToTable(tableName, s.getCurrDbName());

            // Create a list of rows that match the condition (if present)
            List<Row> filteredRows;
            if (whereQuery) {
                 filteredRows = table.filterRows(this.condition);
            } else {
                filteredRows = table.getRows();
            }

            // Check if any rows match the condition
            if (filteredRows.isEmpty()) {
                return "[OK] No rows found";
            }

            // Create a list of columns to display based on attributes or wildcard
            List<Column> columnsToDisplay;
            if (hasWildcard()) {
                // produces a list of all the columns in the table
                columnsToDisplay = table.getColumns();
            } else {
                columnsToDisplay = new ArrayList<>();
                for (String attributeName : attributeList) {
                    Column column = table.getColumn(attributeName);
                    if (column == null) {
                        return "[ERROR] Column " + attributeName + " does not exist in the table " + this.tableName;
                    }
                    columnsToDisplay.add(column);
                }
            }

            // Create a list of rows to display
            List<List<String>> rowsToDisplay = new ArrayList<>();
            for (Row row : filteredRows) {
                List<String> rowValues = new ArrayList<>();

                for (Column column : columnsToDisplay) {
                    int columnIndex = table.getColumnIndex(column.getName());
                    if (columnIndex == -1) {
                        // handle case where column is not found
                    } else {
                        DataValue value = row.getValues().get(columnIndex);
                        rowValues.add(value.getValue());
                    }
                }
                rowsToDisplay.add(rowValues);
            }

            // Format the result string
            StringBuilder resultBuilder = new StringBuilder("[OK]\n");
            List<String> headerRow = new ArrayList<>();
            for (Column column : columnsToDisplay) {
                headerRow.add(column.getName());
            }
            resultBuilder.append(String.join("\t", headerRow)).append("\n");
            for (List<String> rowValues : rowsToDisplay) {
                resultBuilder.append(String.join("\t", rowValues)).append("\n");
            }
            return resultBuilder.toString().trim();

        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] Failed to retrieve data from database";
        }
    }}
