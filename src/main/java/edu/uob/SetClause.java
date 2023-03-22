package edu.uob;

public class SetClause {
    private String attributeName;
    private DataValue value;

    public SetClause(String attributeName, DataValue value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public DataValue getValue() {
        return value;
    }
}
