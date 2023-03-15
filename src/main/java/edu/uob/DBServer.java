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

        // write a method that reads in the data from the sample data file using the Java File IO API.
        String fileName = storageFolderPath + File.separator + "PeopleDB" + File.separator + "people.tab";
        ReadFromFile r = new ReadFromFile(fileName);
        if (r.readFileContents() != null) {

            // read in data from file and store each line in an ArrayList of strings
            ArrayList<String> tableDataFromFile = r.readFileContents();
            Table newTable = createTableDataStructure(tableDataFromFile);

            // this is the key for adding the table to the database
            String tableName = "people";
            Database newDatabase = new Database();
            newDatabase.addTableToDb(tableName, newTable);

            // get key-value pair associated database
            HashMap<String, Table> databaseTables = newDatabase.getDbTables();

            writeToFileSystem(newTable, "testTableName", "testDatabaseName");
        }
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    // TODO rename this method
    public void writeToFileSystem(Table tableToAddToFile, String tableName, String databaseName) {

        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        File databasesFolder = new File(storageFolderPath);
        File databasePath = new File(storageFolderPath + File.separator + databaseName);

        // if the databases folder doesn't exist then make it
        if (!databasesFolder.exists()) {
            databasesFolder.mkdir();
        }

        // if the target database doesn't exist then make it
        if (!databasePath.exists()) {
            databasePath.mkdir();
            System.out.println("Folder created successfully");
        } else {
            System.out.println("Folder already exists");
        }

        try {
            File tableFile = new File(databasePath + File.separator + tableName);
            if (tableFile.createNewFile()) {
                System.out.println("File created: " + tableFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error: unable to create ." + tableName + "file.");
            // TODO investigate this.
            e.printStackTrace();
        }

        HashMap<String, String> firstRow =  tableToAddToFile.getRowData(0);
        ArrayList<String> columnAttributes = new ArrayList<>();
        Set<String> tableKeys = firstRow.keySet();
        columnAttributes.add("id");
        for (String key : tableKeys) {
            if (!key.equals("id")) {
                columnAttributes.add(key);
            }
        }

        if (databasePath.exists()) {
            try {
                File tableFile = new File(databasePath + File.separator + tableName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
                for (String attribute : columnAttributes) {
                    writer.write(attribute + "\t");
                }
                writer.newLine();

                ArrayList<HashMap<String, String>> allData = tableToAddToFile.getAllTableData();

                for (HashMap<String, String> eachItem : allData) {
                    // for each HashMap, add the object to the correct column by matching the key
                    System.out.println("UPDATE HERE");
                }


                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Table createTableDataStructure(ArrayList<String> tableData) {
        String[] tableAttributes = tableData.get(0).split("\t");
        Table newTable = new Table();
        for (int i=0; i<tableData.size(); i++) {
            if (i > 0) {
                String[] tableRowData = tableData.get(i).split("\t");
                HashMap<String, String> newRowHashMap = new HashMap<>();
                for (int k=0; k<tableRowData.length; k++) {
                    // {id: 1, name: bob, age: 21, email:@net}
                    newRowHashMap.put(tableAttributes[k], tableRowData[k]);
                }
                newTable.addRow(newRowHashMap);
            }
        }
        ArrayList<String> newColumn = newTable.getColumnData("Age");
        for (String item : newColumn) {
            System.out.println(item);
        }
        return newTable;
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
