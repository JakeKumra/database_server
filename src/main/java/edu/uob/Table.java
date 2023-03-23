package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String name;
    private final ArrayList<Column> columns;
    private String[] headers;
    private final ArrayList<Row> rows;

    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public List<String> getHeadersList() {
        List<String> headersList = new ArrayList<>();
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                headersList.add(headers[i]);
            }
        }
        return headersList;
    }

    public void setHeaders(String [] headers) {
        this.headers = headers;
    }

    public Column getColumn(String columnName) {

        int columnIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            // Column not found
            return null;
        }

        Column column = new Column(columnName);

        for (Row row : rows) {
            List<DataValue> rowValues = row.getValues();
            if (columnIndex >= rowValues.size()) {
                // Invalid row
                continue;
            }
            DataValue columnValue = rowValues.get(columnIndex);
            column.addValue(columnValue);
        }
        return column;
    }

    public int getColumnIndex(String columnName) {
        int columnIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public List<Row> filterRows(Condition condition) {
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (evaluateCondition(row, condition)) {
                result.add(row);
            }
        }
        return result;
    }

    public int deleteRows(Condition condition) {
        int count = 0;
        if (rows != null) {
            for (int i=0; i<rows.size(); i++) {
                if (evaluateCondition(rows.get(i), condition)) {
                    count++;
                    rows.remove(i);
                }
            }
        }
        return count;
    }

    private boolean evaluateCondition(Row row, Condition condition) {

        try {
            String operator = condition.getOperator();
            String columnName = condition.getColumn();
            String conditionValue = removeQuotes(condition.getValue());
            String columnValue = row.getValueByCol(columnName);

            if (condition.isSimpleComparison()) {

                if (operator.equals("<")) {
                    if (Double.parseDouble(columnValue) < Double.parseDouble(conditionValue)) {
                        return true;
                    }
                } else if (operator.equals("<=")) {
                    if (Double.parseDouble(columnValue) <= Double.parseDouble(conditionValue)) {
                        return true;
                    }
                } else if (operator.equals(">")) {
                    if (Double.parseDouble(columnValue) > Double.parseDouble(conditionValue)) {
                        return true;
                    }
                 } else if (operator.equals(">=")) {
                    if (Double.parseDouble(columnValue) >= Double.parseDouble(conditionValue)) {
                        return true;
                    }
                } else if (operator.equals("==")) {
                    if (columnValue.equals(conditionValue)) {
                        return true;
                    }
                } else if (operator.equals("!=")) {
                    if (!columnValue.equals(conditionValue)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                boolean leftResult = evaluateCondition(row, condition.getNested());
                boolean rightResult = evaluateCondition(row, condition.getRight());
                String boolOp = condition.getBoolOp();
                switch (boolOp.toUpperCase()) {
                    case "AND":
                        return leftResult && rightResult;
                    case "OR":
                        return leftResult || rightResult;
                    default:
                        return false;
                }
            }
        } catch (NumberFormatException e){
            return false;
        }
        return false;
    }

    public static String removeQuotes(String input) {
        if (input.startsWith("'") && input.endsWith("'")) {
            return input.substring(1, input.length() - 1);
        } else {
            return input;
        }
    }

    public int updateRows(String attributeName, DataValue newValue, Condition condition) {

        int numRowsUpdated = 0;
        int attributeIndex = getColumnIndex(attributeName);
        if (attributeIndex == -1) {
            return numRowsUpdated;
        }

        List<Row> filteredRows = filterRows(condition);

        for (Row row : filteredRows) {
            DataValue oldValue = row.getDataValue(attributeIndex);
            if (oldValue == null || !oldValue.getValue().equals(newValue.getValue())) {
                row.setDataValue(attributeIndex, newValue);
                numRowsUpdated++;
            }
        }
        return numRowsUpdated;
    }
}


