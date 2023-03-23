package edu.uob;

public class UseCMD extends DBcmd {
    private final String dbName;
    public UseCMD(String dbName) {
        super();
        this.dbName = dbName;
        this.parseError = false;
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }
        try {
            Database dbFromFile = s.getDatabaseFromFile(dbName);
            // Check if the database exists
            if (dbFromFile == null) {
                return "[ERROR] Database " + dbName + " does not exist";
            }
            // Set the current database
            s.setCurrentDatabase(dbFromFile);
            return "[OK] Database changed to " + dbName;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "[ERROR] has occured";
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return "[ERROR] has occured";
        }
    }
}

