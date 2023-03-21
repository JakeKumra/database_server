package edu.uob;
import java.util.List;

// TODO check this class
public abstract class DBcmd {

    protected String errorMessage;
    protected boolean parseError;

    public DBcmd() {
        this.parseError = false;
        this.errorMessage = null;
    }

    public void setParseError() {
        this.parseError = true;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public abstract String query(DBServer s);

}
