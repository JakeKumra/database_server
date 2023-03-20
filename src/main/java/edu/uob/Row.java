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
}
