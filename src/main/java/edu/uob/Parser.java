package edu.uob;

import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
    private ArrayList<String> tokens;
    private int pos;

    public Parser(ArrayList<String> tokens) {
        this.tokens = tokens;
        pos = 0;
    }

    public DBcmd parse() throws ParseException {
        DBcmd cmd = null;
        //String token = getNextToken();
        String token = getCurrentToken();

        if (token.equalsIgnoreCase("USE")) {
            cmd = parseUse();
//        } else if (token.equalsIgnoreCase("CREATE")) {
//            cmd = parseCreate();
//        } else if (token.equalsIgnoreCase("INSERT")) {
//            cmd = parseInsert();
//        } else if (token.equalsIgnoreCase("SELECT")) {
//            cmd = parseSelect();
//        } else if (token.equalsIgnoreCase("UPDATE")) {
//            cmd = parseUpdate();
//        } else if (token.equalsIgnoreCase("ALTER")) {
//            cmd = parseAlter();
//        } else if (token.equalsIgnoreCase("DELETE")) {
//            cmd = parseDelete();
//        } else if (token.equalsIgnoreCase("DROP")) {
//            cmd = parseDrop();
//        } else if (token.equalsIgnoreCase("JOIN")) {
//            cmd = parseJoin();
        } else {
            throw new ParseException("Invalid command: " + token, pos);
        }
        return cmd;
    }

    private UseCMD parseUse() throws ParseException {
        // Consume the "USE" keyword
        if (!getNextToken().equalsIgnoreCase("USE")) {
            throw new ParseException("Expected USE keyword", pos);
        }

        // Consume the database name, if present
        String databaseName = null;
        String nextToken = getNextToken();
        if (nextToken.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            databaseName = nextToken;
        } else {
            // TODO should this fail the parse?
            pos--;

        }

        // TODO is this correct?
         if (!getNextToken().equalsIgnoreCase(";")) {
            throw new ParseException("Expected ; at end of command", pos);
        }

        // Create and return the UseCMD object
        return new UseCMD(databaseName);
    }

    private SelectCMD parseSelect() throws ParseException {
        SelectCMD cmd = new SelectCMD();
        // Parse the rest of the command here
        // Set the attributes of the SelectCMD object based on the tokens
        return cmd;
    }

    private AlterCMD parseAlter() throws ParseException {
        AlterCMD cmd = new AlterCMD();
        // Parse the rest of the command here
        // Set the attributes of the AlterCMD object based on the tokens
        return cmd;
    }

    private InsertCMD parseInsert() throws ParseException {
        InsertCMD cmd = new InsertCMD();
        // Parse the rest of the command here
        // Set the attributes of the InsertCMD object based on the tokens
        return cmd;
    }

    private UpdateCMD parseUpdate() throws ParseException {
        UpdateCMD cmd = new UpdateCMD();
        // Parse the rest of the command here
        // Set the attributes of the UpdateCMD object based on the tokens
        return cmd;
    }

    private String getNextToken() throws ParseException {
        if (pos >= tokens.size()) {
            throw new ParseException("Unexpected end of input", pos);
        }
        return tokens.get(pos++);
    }

    private String getCurrentToken() throws ParseException {
        if (pos >= tokens.size()) {
            throw new ParseException("pos is beyond the size of tokens", pos);
        }
        return tokens.get(pos);
    }
}



