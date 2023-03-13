package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

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
            ArrayList<String> tableData = r.readFileContents();
            Table newTable = createTableDataStructure(tableData);
        }

        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
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
