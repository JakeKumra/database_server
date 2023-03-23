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

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String column) {
        this.header = column;
    }
}

