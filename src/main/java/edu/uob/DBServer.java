package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;


/** This class implements the DB server. */
public class DBServer {

    private Database currentDatabase;

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
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    // TODO check the .tab parts of this I'm not sure about it
    public Database getDatabaseFromFile(String dbName) {
        String databasesPath = new FileManager().getDbPath();
        File dbFolder = new File(databasesPath, dbName);

        if (dbFolder.exists() && dbFolder.isDirectory()) {

            Database database = new Database(dbFolder.getName());
            File[] allFilesInDb = dbFolder.listFiles();
            for (File file : allFilesInDb) {
                if (file.isFile() && file.getName().endsWith(".tab")) {
                    String tableName = file.getName().substring(0, file.getName().lastIndexOf(".tab"));
                    try {
                        Table newTableFromDb = parseFileToTable(tableName, dbName);
                        database.addTable(newTableFromDb.getName(), newTableFromDb);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error creating newTableFromDb");
                    }
                }
            }
            return database;
        } else {
            System.out.println("Unable to locate database in file system");
            return null;
        }
    }

    public Database getCurrentDatabase() {
        return this.currentDatabase;
    }

    public String getTableNames() {
        StringBuilder sb = new StringBuilder();
        FileManager FM = new FileManager();
        File dbFolder = new File(FM.getDbPath() + File.separator + getCurrDbName());
        File[] allFiles = dbFolder.listFiles();
        for (File file : allFiles) {
            sb.append(file.getName()).append(" ");
        }
        String tableNames = sb.toString();
        return tableNames;
    }

    public String getCurrDbName() {
        if (this.currentDatabase != null) {
            return this.currentDatabase.getDatabaseName();
        } else {
            return null;
        }
    }

    public void setCurrentDatabase(Database dbName) {
        this.currentDatabase = dbName;
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

        System.out.println("INSIDE");

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
        writer.close();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        Lexer l = new Lexer(command);
        ArrayList<String> tokens = l.tokenizeInput();
        Parser p = new Parser(tokens);
        try {
            DBcmd cmd = p.parse();
            return cmd.query(this);
        } catch (ParseException e) {e.printStackTrace();}
        // TODO check this return statement
        return "An [ERROR] has occurred";
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
