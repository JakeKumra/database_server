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

    // TODO delete if necessary
//    public ArrayList<String> getOneColumn(String attributeName) {
//        // TODO can change to ArrayList of Datavalues if necessary
//        ArrayList<String> columnList = new ArrayList<>();
//        columnList.add(attributeName);
//        ArrayList<Row> allRows = getRows();
//        for (int i=0; i<allRows.size(); i++) {
//            int rowLength = allRows.get(i).getRowLength();
//            ArrayList<DataValue> allValuesInRow = allRows.get(i).getValues();
//            for (int j=0; j<rowLength; j++) {
//                if (allValuesInRow.get(j).getHeader().equals(attributeName)) {
//                    columnList.add(allValuesInRow.get(j).getValue());
//                }
//            }
//        }
//        return columnList;
//    }

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


    // TODO delete this if unused?
//    public boolean attributeFound (String attribute) {
//        String [] headersList = this.getHeaders();
//        boolean attributeFound = false;
//        for (int i=0; i< getHeaders().length; i++) {
//            if (headersList[i].equals(attribute)) {
//                attributeFound = true;
//            }
//        }
//        return attributeFound;
//    }

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
        System.out.println("INSIDE FILTER ROWS");
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (evaluateCondition(row, condition)) {
                result.add(row);
            }
        }
        return result;
    }

    private boolean evaluateCondition(Row row, Condition condition) {

        String operator = condition.getOperator();
        String columnName = condition.getColumn();
        String conditionValue = removeQuotes(condition.getValue());
        String columnValue = row.getValueByCol(columnName);

        System.out.println("operator: " + operator + "\n");
        System.out.println("column: " + columnName + "\n");
        System.out.println("value: " + conditionValue + "\n");
        System.out.println("Column value: "+ columnValue);

        // ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "

        if (condition.isSimpleComparison()) {

            if (operator.equals("<")) {
                return false;
//                        columnValue < conditionValue;
//            } else if (operator.equals("<=")) {
//                return columnValue <= conditionValue;
//            } else if (operator.equals(">")) {
//                return columnValue > conditionValue;
//            } else if (operator.equals(">=")) {
//                return columnValue >= conditionValue;
            } else if (operator.equals("==")) {
                if (columnValue.equals(conditionValue)) {
                    return true;
                }
            } else if (operator.equals("!=")) {
                if (!columnValue.equals(conditionValue)) {
                    return true;
                }
            } else {
                throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        } else {
            boolean leftResult = evaluateCondition(row, condition.getNested());
            boolean rightResult = evaluateCondition(row, condition.getRight());
            String boolOp = condition.getBoolOp();
            switch (boolOp) {
                case "AND":
                    return leftResult && rightResult;
                case "OR":
                    return leftResult || rightResult;
                default:
                    throw new IllegalArgumentException("Invalid boolean operator: " + boolOp);
            }
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

    public String convertTableToString() {
        StringBuilder sb = new StringBuilder();

        // add the column headers
        Row firstRow = rows.get(0);
        for (DataValue dv : firstRow.getValues()) {
            sb.append(dv.getHeader().toString());
            sb.append('\t');
        }
        sb.append('\n');

        // add the rest of the rows
        for (Row row : rows) {
            for (DataValue dv : row.getValues()) {
                sb.append(dv.getValue().toString());
                sb.append('\t');
            }
            sb.append('\n');
        }
        String stringCopyOfTable = sb.toString();
        return stringCopyOfTable;
    }

    public int updateRows(String attributeName, DataValue newValue, Condition condition) {

        int numRowsUpdated = 0;
        int attributeIndex = getColumnIndex(attributeName);

        if (attributeIndex == -1) {
            // Attribute not found
            // >>>>>>>>>>>>>> THERE'S AN ISSUE HERE <<<<<<<<<<<<<<<<
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


