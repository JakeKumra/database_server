package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private ArrayList<Column> columns;
    private String[] headers;
    private ArrayList<Row> rows;

    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public void setHeaders(String [] headers) {
        this.headers = headers;
    }

    public ArrayList<String> getOneColumn(String attributeName) {
        // TODO can change to ArrayList of Datavalues if necessary
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(attributeName);
        ArrayList<Row> allRows = getRows();
        for (int i=0; i<allRows.size(); i++) {
            int rowLength = allRows.get(i).getRowLength();
            ArrayList<DataValue> allValuesInRow = allRows.get(i).getValues();
            for (int j=0; j<rowLength; j++) {
                if (allValuesInRow.get(j).getHeader().equals(attributeName)) {
                    columnList.add(allValuesInRow.get(j).getValue());
                }
            }
        }
        return columnList;
    }

    public boolean attributeFound (String attribute) {

        String [] headersList = this.getHeaders();
        boolean attributeFound = false;
        for (int i=0; i< getHeaders().length; i++) {
            if (headersList[i].equals(attribute)) {
                attributeFound = true;
            }
        }
        return attributeFound;
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
}   }
