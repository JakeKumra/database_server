package edu.uob;
import java.util.List;

public abstract class DBcmd {
    protected List<Condition> conditions;
    protected List<String> colNames;
    protected List<String> tableNames;
    protected String DBname;
    protected String commandType;

    public abstract String query(DBServer s);
}
