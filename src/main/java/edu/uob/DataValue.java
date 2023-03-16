package edu.uob;

public class DataValue {
    private String value;
    private String column;

    public DataValue(String value, String column) {
        this.value = value;
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}

