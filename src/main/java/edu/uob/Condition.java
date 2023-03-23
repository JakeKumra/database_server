package edu.uob;

public class Condition {
    private String column;
    private String operator;
    private String value;
    private Condition nested;
    private Condition right;
    private String boolOp;

    public Condition(String column, String operator, String value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public Condition(Condition nested, String boolOp, Condition right) {
        this.nested = nested;
        this.boolOp = boolOp;
        this.right = right;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperator() {
        return operator;
    }
    public String getValue() {
        return value;
    }
    public Condition getNested() {
        return nested;
    }

    public Condition getRight() {
        return right;
    }
    public String getBoolOp() {
        return boolOp;
    }

    public boolean isSimpleComparison() {
        return column != null && operator != null && value != null;
    }

    @Override
    public String toString() {
        if (isSimpleComparison()) {
            return column + " " + operator + " " + value;
        } else {
            return "(" + nested + ") " + boolOp + " (" + right + ")";
        }
    }
}

