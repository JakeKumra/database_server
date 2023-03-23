package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JoinCMD extends DBcmd {
    private String tableName1;
    private String tableName2;
    private String attributeName1;
    private String attributeName2;

    public JoinCMD() {
        super();
    }

    public void setTableName1(String tableName1) {
        this.tableName1 = tableName1;
    }

    public void setTableName2(String tableName2) {
        this.tableName2 = tableName2;
    }

    public void setAttributeName1(String attributeName1) {
        this.attributeName1 = attributeName1;
    }

    public void setAttributeName2(String attributeName2) {
        this.attributeName2 = attributeName2;
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        } else if (s.getCurrentDatabase() == null) {
            return "[ERROR] no database has been selected for use";
        }
        String databasePath = new FileManager().getDbPath() + File.separator + s.getCurrDbName();
        File table1File = new File (databasePath + File.separator + tableName1);
        File table2File = new File (databasePath + File.separator + tableName2);

        if (!table1File.exists() || !table2File.exists()) {
            return "[ERROR] tables can't be found in database";
        }
        try {
            Table table1 = new FileManager().parseFileToTable(tableName1, s.getCurrDbName());
            Table table2 = new FileManager().parseFileToTable(tableName2, s.getCurrDbName());
            if (table1 == null || table2 == null) {
                return "[ERROR] One or both tables not found";
            }

            StringBuilder result = new StringBuilder();
            result.append("[OK] \n").append("id \t");
            List<String> headers1 = table1.getHeadersList();
            List<String> headers2 = table2.getHeadersList();
            int index1 = headers1.indexOf(attributeName1);
            int index2 = headers2.indexOf(attributeName2);
            int id = 1;

            for (String header : headers1) {
                if (!header.equals(attributeName1) && !header.equalsIgnoreCase("id")) {
                    result.append(tableName1 + "." + header + "\t");
                }
            }
            for (String header : headers2) {
                if (!header.equals(attributeName2) && !header.equalsIgnoreCase("id")) {
                    result.append(tableName2 + "." + header + "\t");
                }
            }
            result.append("\n");
            // Iterate over the rows of the two tables to find matching records
            for (Row rowTable1 : table1.getRows()) {
                for (Row rowTable2 : table2.getRows()) {
                    // Check if the values of the matching headers are equal
                    if (    rowTable1.getValues().get(index1).getValue()
                            .equals(rowTable2.getValues().get(index2).getValue())) {
                        // Concatenate the matching rows
                        Row resultRow = new Row(id++, new ArrayList<>());
                        resultRow.getValues().addAll(rowTable1.getValuesExcluding(attributeName1));
                        resultRow.getValues().addAll(rowTable2.getValuesExcluding(attributeName2));
                        result.append(resultRow.toString()).append("\n");
                    }
                }
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] tables can't be joined";
        }
    }
}
