package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class Row {
    private int id;
    private ArrayList<DataValue> values;

    public Row(int id, ArrayList<DataValue> values) {
        this.id = id;
        this.values = values;
    }

    public void printRow() {
        for (DataValue val : values) {
            System.out.println(val.getValue());
        }
    }

    public int getRowLength() {
        return values.size();
    }

    public int getId() {
        return id;
    }

    public ArrayList<DataValue> getValues() {
        return values;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValues(ArrayList<DataValue> values) {
        this.values = values;
    }

    public void addValue(DataValue value) {
        values.add(value);
    }

    public DataValue getValue(int index) {
        return values.get(index);
    }

    public String getValueByCol(String colName) {
        for (int i=0; i<values.size(); i++) {
            if (values.get(i).getHeader().equals(colName)) {
                return values.get(i).getValue();
            }
        }
        return null;
    }


//    public int getInt(String columnName) {
//        int columnIndex = getColumnIndex(columnName);
//        if (columnIndex == -1) {
//            // Column not found
//            throw new IllegalArgumentException("Column not found: " + columnName);
//        }
//        return getInt(columnIndex);
//    }
//
//    private int getColumnIndex(String columnName) {
//        for (int i = 0; i < columns.length; i++) {
//            if (columns[i].equals(columnName)) {
//                return i;
//            }
//        }
//        return -1;  // Column not found
//    }



}
