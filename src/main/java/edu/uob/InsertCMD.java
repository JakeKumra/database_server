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

    // TODO refactor this function as it's not DRY

    @Override
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
                    ArrayList<DataValue> valuesInRow = new ArrayList<>();
                    DataValue firstValueInRow = new DataValue("1", "id");
                    valuesInRow.add(firstValueInRow);
                    for (int i=0; i<values.size(); i++) {
                        DataValue nextValueinRow = new DataValue(values.get(i), tableHeaders[i]);
                        valuesInRow.add(nextValueinRow);
                    }
                    Row newRow = new Row(1, valuesInRow);
                    tableFromFile.addRow(newRow);
                    FileManager FM = new FileManager();
                    String path = FM.getDbPath() + File.separator + s.getCurrDbName() + File.separator + tableName;
                    s.parseTableToFile(tableFromFile, path);
                } else {
                    // table contains some row(s) already
                    ArrayList<Row> rows = tableFromFile.getRows();
                    Row lastRow = rows.get(rows.size() - 1);
                    int int_id = lastRow.getId() + 1;
                    String string_id = Integer.toString(int_id);

                    // create a new row and add it
                    String[] tableHeaders = tableFromFile.getHeaders();
                    ArrayList<DataValue> valuesInRow = new ArrayList<>();
                    DataValue firstValueInRow = new DataValue(string_id, "id");
                    valuesInRow.add(firstValueInRow);
                    for (int i=0; i<values.size(); i++) {
                        DataValue nextValueinRow = new DataValue(values.get(i), tableHeaders[i]);
                        valuesInRow.add(nextValueinRow);
                    }
                    Row newRow = new Row(int_id, valuesInRow);
                    tableFromFile.addRow(newRow);
                    FileManager FM = new FileManager();
                    String path = FM.getDbPath() + File.separator + s.getCurrDbName() + File.separator + tableName;
                    s.parseTableToFile(tableFromFile, path);
                }
                return "[OK] " + values + " added to " + "table " + tableName + " inside database " + s.getCurrDbName();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading table from file inside InsertCMD");
            }
        }
        return "[ERROR]";
    }
}
