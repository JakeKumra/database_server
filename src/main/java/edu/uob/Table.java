package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private ArrayList<Column> columns;
    private ArrayList<Row> rows;

    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
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


}
