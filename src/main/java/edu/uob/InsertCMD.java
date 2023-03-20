package edu.uob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertCMD extends DBcmd {

    String tableName;
    List<String> values;

    public InsertCMD (String name, List<String> values) {
        this.tableName = name;
        this.values = values;
    }

    public String query(DBServer s) {

        if (s.getCurrentDatabase() == null) {
            return "[ERROR] No database has been selected to use";
        }
        File dbFolder = new File(new FileManager().getDbPath() + File.separator + s.getCurrDbName());
        if (!new File(dbFolder, tableName).exists()) {
            // TODO double check this is correct to return an error
            return "[ERROR] table " + tableName + " can't be found in database";
        } else {
            try {
                Table tableFromFile = s.parseFileToTable(tableName, s.getCurrDbName());
                if (tableFromFile.getRows().size() == 0) {

                    String[] tableHeaders = tableFromFile.getHeaders();

                    for (String h : tableHeaders) {
                        System.out.println(h);
                    }

                    // create a new row and write it to the file then send response to user
                    // how to create a new row?
                    ArrayList<DataValue> valuesInRow = new ArrayList<>();
                    DataValue firstVal = new DataValue("1", "id");
                    valuesInRow.add(firstVal);
                    for (String value : values) {
                        // create a new DataValue and add it to the valuesInRow
                        DataValue v = new DataValue(value, "TODO");
                        valuesInRow.add(v);
                    }
                    // Row newRow = new Row();


                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading table from file inside InsertCMD");
            }

            // find the id value of the current table?
            // pull the table into data structure
            // manipulate and add new row
        }


//        System.out.println(tableName);
//        for (String value : values) {
//            System.out.println(value);
//        }

        String response = "query called inside InsertCMD";
        return response;
    }
}
