package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
            Files.createDirectories(Paths.get(storageFolderPath + File.separator + "testDb"));

            String filePath = storageFolderPath + File.separator + "testDb" + File.separator + "testFile";
            File file = new File(filePath);
            file.createNewFile();
            Table newTable = parseFileToTable("people.tab");
            parseTableToFile(newTable, filePath);

        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public Table parseFileToTable(String fileName) throws IOException {

        String filePath = storageFolderPath + File.separator + "PeopleDB" + File.separator + fileName;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String[] headers = null;

        ArrayList<Row> rows = new ArrayList<>();

        // line will store the newly read in line
        while ( (line = reader.readLine() ) != null) {
            String[] values = line.split("\t");
            if (headers == null) {
                // first row is headers i.e. column attribute names
                headers = values;
            } else {
                ArrayList<DataValue> allValuesInRow = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    DataValue dataValue = new DataValue(values[i], headers[i]);
                    allValuesInRow.add(dataValue);
                }
                Row row = new Row(
                        Integer.parseInt(allValuesInRow.get(0).getValue()),
                        allValuesInRow);
                rows.add(row);
            }
        }
        reader.close();

        // create a table with the parsed headers and rows
        Table table = new Table(fileName);
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
        ArrayList<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            writer.write(columns.get(i).getName());
            if (i < columns.size() - 1) {
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

        writer.close();
    }





    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {

        // TODO implement your server logic here
        return "";
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
