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
