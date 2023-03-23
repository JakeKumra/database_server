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

    public int getId() {
        return id;
    }

    public ArrayList<DataValue> getValues() {
        return values;
    }

    public List<DataValue> getValuesExcluding(String... headersToExclude) {
        List<DataValue> valuesExcluding = new ArrayList<>();
        for (DataValue value : values) {
            boolean exclude = false;
            for (String headerToExclude : headersToExclude) {
                if (value.getHeader().equals(headerToExclude)) {
                    exclude = true;
                    break;
                }
            }
            if (!exclude) {
                valuesExcluding.add(value);
            }
        }
        return valuesExcluding;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i).toString());
            if (i < values.size() - 1) {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    public String getValueByCol(String colName) {
        for (int i=0; i<values.size(); i++) {
            if (values.get(i).getHeader().equals(colName)) {
                return values.get(i).getValue();
            }
        }
        return null;
    }

    public DataValue getDataValue(int index) {
        return values.get(index);
    }

    public void setDataValue(int index, DataValue value) {
        values.set(index, value);
    }
}
