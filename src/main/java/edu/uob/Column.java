package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Column {

    private String name;
    private List<DataValue> values;

    public Column(String name) {
        this.name = name;
        this.values = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addValue(DataValue value) {
        values.add(value);
    }

    public List<DataValue> getValues() {
        return values;
    }

    // Other methods as needed...
}
