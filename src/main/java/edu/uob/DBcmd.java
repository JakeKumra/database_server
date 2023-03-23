package edu.uob;

public abstract class DBcmd {

    protected String errorMessage;
    protected boolean parseError;

    public DBcmd() {
        this.parseError = false;
        this.errorMessage = null;
    }

    public void setError(String errorMessage) {
        this.parseError = true;
        this.errorMessage = errorMessage;
    }

    public abstract String query(DBServer s);

}
