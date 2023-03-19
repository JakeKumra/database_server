package edu.uob;
import java.nio.file.Paths;


public class FileManager {

    private String storageFolderPath;

    public FileManager() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
    }

    public String getDatabasesPath() {
        return storageFolderPath;
    }

    // TODO add other file functions here

}
