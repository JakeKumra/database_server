package edu.uob;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReadFromFile {

    private String fileName;
    private String storageFolderPath;
    public ReadFromFile(String fileToRead) {
        this.fileName = fileToRead;
    }

    public ArrayList<String> readFileContents() {
        File fileToOpen = new File(fileName);
        if (fileToOpen.exists()){
            // read in the contents of the file
            try  {
                FileReader reader = new FileReader(fileName);
                BufferedReader buffReader = new BufferedReader(reader);
                String eachLine = buffReader.readLine();
                ArrayList<String> tableData = new ArrayList<>();
                while (eachLine != null) {
                    tableData.add(eachLine);
                    eachLine = buffReader.readLine();
                }
                buffReader.close();
                return tableData;
            } catch (FileNotFoundException e) {
                // TODO log to standard error output instead of standard output?
                System.out.println(e);
            } catch (IOException e) {
                // TODO log to standard error output instead of standard output?
                System.out.println(e);
            }
        } else {
            // TODO log to standard error output instead of standard output?
            System.out.println("This file doesn't exist");
        }
        return null;
    }
}
