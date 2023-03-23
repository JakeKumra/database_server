package edu.uob;

public class DataValue {
    private String value;
    private String header;

    public DataValue(String value, String column) {
        this.value = value;
        this.header = column;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getHeader() {
        return header;
    }
}

