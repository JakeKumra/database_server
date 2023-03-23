package edu.uob;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;


public class FileManager {

    private String storageFolderPath;

    public FileManager() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
    }

    public String getDbPath() {
        return storageFolderPath;
    }

    public Table parseFileToTable(String fileName, String dbName) throws IOException {
        String filePath = new FileManager().getDbPath() + File.separator + dbName + File.separator + fileName;

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String[] headers = null;
        ArrayList<Row> rows = new ArrayList<>();
        // line will store the newly read in line
        while ( (line = reader.readLine() ) != null) {
            String[] values = line.split("\\s+");
            if (headers == null) {
                // first row is headers i.e. column attribute names
                headers = values;
            } else {
                ArrayList<DataValue> allValuesInRow = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    DataValue dataValue = new DataValue(values[i], headers[i]);
                    allValuesInRow.add(dataValue);
                }
                int id_num = Integer.parseInt(allValuesInRow.get(0).getValue());
                Row row = new Row(id_num, allValuesInRow);
                rows.add(row);
            }
        }

        reader.close();

        // create a table with the parsed headers and rows
        Table table = new Table(fileName);
        table.setHeaders(headers);
        for (String header : headers) {
            Column column = new Column(header);
            table.addColumn(column);
        }

        for (Row row : rows) {
            table.addRow(row);
        }
        return table;
    }
    public void parseTableToFile(Table table, String filePath) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        // write the headers
        String[] headers = table.getHeaders();
        for (int i=0; i< headers.length; i++) {
            writer.write(headers[i]);
            if (i < headers.length - 1) {
                writer.write("\t");
            }
        }
        writer.newLine();

        // write the rows
        ArrayList<Row> rows = table.getRows();
        for (Row row : rows) {
            ArrayList<DataValue> values = row.getValues();
            for (int i = 0; i < values.size(); i++) {
                writer.write(values.get(i).getValue());
                if (i < values.size() - 1) {
                    writer.write("\t");
                }
            }
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
