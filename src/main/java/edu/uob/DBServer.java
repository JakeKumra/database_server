package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/** This class implements the DB server. */
public class DBServer {

    private Database currentDatabase;

    private List<String> reservedWords;

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
                        Table newTableFromDb = new FileManager().parseFileToTable(tableName, dbName);
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
