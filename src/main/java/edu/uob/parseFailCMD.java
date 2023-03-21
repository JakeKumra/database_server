package edu.uob;

public class parseFailCMD extends DBcmd {

    private String errorMessage;

    public parseFailCMD(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String query(DBServer s) {
        return errorMessage;
    }
}
