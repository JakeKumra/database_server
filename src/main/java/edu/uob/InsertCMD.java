package edu.uob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertCMD extends DBcmd {

    String tableName;
    List<String> values;

    public InsertCMD (String name, List<String> values) {
        super();
        this.tableName = name;
        this.values = values;
    }

    private void addNewRow(Table table, int id) {
        String[] tableHeaders = table.getHeaders();
        ArrayList<DataValue> valuesInRow = new ArrayList<>();
        DataValue firstValueInRow = new DataValue(Integer.toString(id), "id");
        valuesInRow.add(firstValueInRow);
        for (int i = 0; i < values.size(); i++) {
            DataValue nextValueInRow = new DataValue(values.get(i), tableHeaders[i]);
            valuesInRow.add(nextValueInRow);
        }
        Row newRow = new Row(id, valuesInRow);
        table.addRow(newRow);
    }

    @Override
    public String query(DBServer s) {
        if (parseError) {
            return errorMessage;
        }
        if (s.getCurrentDatabase() == null) {
            return "[ERROR] No database has been selected to use";
        }
        File dbFolder = new File(new FileManager().getDbPath() + File.separator + s.getCurrDbName());
        if (!new File(dbFolder, tableName).exists()) {
            return "[ERROR] table " + tableName + " can't be found in database";
        }

        try {
            Table tableFromFile = new FileManager().parseFileToTable(tableName, s.getCurrDbName());
            if (tableFromFile.getHeaders().length != values.size() + 1) {
                return "[ERROR] number of values does not match table attributes";
            }
            if (tableFromFile.getRows().size() == 0) {
                addNewRow(tableFromFile, 1);
            } else {
                ArrayList<Row> rows = tableFromFile.getRows();
                Row lastRow = rows.get(rows.size() - 1);
                int int_id = lastRow.getId() + 1;
                addNewRow(tableFromFile, int_id);
            }

            FileManager FM = new FileManager();
            String path = FM.getDbPath() + File.separator + s.getCurrDbName() + File.separator + tableName;
            new FileManager().parseTableToFile(tableFromFile, path);

            return String.format("[OK] %s added to table %s inside database %s", values, tableName, s.getCurrDbName());
        } catch (IOException e) {
            e.printStackTrace();
            return "[ERROR] unable to load table from database";
        }
    }
}
