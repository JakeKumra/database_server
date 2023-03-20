package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectCMD extends DBcmd {
    private List<String> attributeList;

    private boolean whereQuery;
    private String tableName;
    // private Condition condition;

    public SelectCMD() {
        this.attributeList = new ArrayList<>();
        this.tableName = null;
        this.whereQuery = false;
        // this.condition = null;
    }

    public void setWhereQuery(boolean bool) {
        this.whereQuery = bool;
    }

    public boolean getWhereQueryStatus() {
        return this.whereQuery;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

//    public Condition getCondition() {
//        return condition;
//    }
//
//    public void setCondition(Condition condition) {
//        this.condition = condition;
//    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public void addAttribute(String attributeName) {
        this.attributeList.add(attributeName);
    }

    public void removeAttribute(String attributeName) {
        this.attributeList.remove(attributeName);
    }

    public boolean hasWildcard() {
        return this.attributeList.size() == 1 && this.attributeList.get(0).equals("*");
    }

    @Override
    public String query(DBServer s) {

        if (s.getCurrentDatabase() == null) {
            return "[ERROR] no database has been selected";
        }

        FileManager FM = new FileManager();
        String tablePath = FM.getDbPath() + File.separator + s.getCurrDbName() + File.separator + tableName;
        if (!new File(tablePath).exists()) {
            return "[ERROR] no table exists within " + s.getCurrDbName();
        }
        // check that the columns exist and if they don't then return error
        // if it does then pull it into memory
        // get the table within current db from memory
        try {
            Table tableToQuery = s.parseFileToTable(tableName, s.getCurrDbName());

            if (this.getWhereQueryStatus() == true) {

                if (this.hasWildcard() == true) {
                    // provide all information from WHERE query
                } else {
                    // process attributes in accordance with the WHERE query

                    // TODO refactor to make this dry
                    for (int i=0; i<attributeList.size(); i++) {
                        if (!tableToQuery.attributeFound(attributeList.get(i))) {
                            return "[ERROR]" + " attribute " + attributeList.get(i) + " not found in table";
                        }
                    }
                }
            } else {
                if (this.hasWildcard() == true) {
                    return "[OK]" + " \n" + tableToQuery.convertTableToString();
                } else {
                    // no WHERE case, no wildcard, but contains attributes
                    // TODO refactor to make this dry
                   String response = new String("[OK]" + "\n");
                    for (int i=0; i<attributeList.size(); i++) {
                        if (!tableToQuery.attributeFound(attributeList.get(i))) {
                            return "[ERROR]" + " attribute " + attributeList.get(i) + " not found in table";
                        }
                        ArrayList<String> columns = tableToQuery.getOneColumn(attributeList.get(i));
                        for (String col : columns) {
                            response += col + "\n";
                        }
                    }
                    return response;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to load table " + tableName + " from file system");
        }
        return "Inside SelectCMD";
    }
}

