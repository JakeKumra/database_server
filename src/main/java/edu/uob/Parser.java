package edu.uob;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private ArrayList<String> tokens;
    private int pos;

    public Parser(ArrayList<String> tokens) {
        this.tokens = tokens;
        pos = 0;
    }

    public DBcmd parse() throws ParseException {
        DBcmd cmd = null;

        String token = getCurrentToken();

        if (token.equalsIgnoreCase("USE")) {
            cmd = parseUse();
        } else if (token.equalsIgnoreCase("CREATE")) {
            cmd = parseCreate();
        } else if (token.equalsIgnoreCase("INSERT")) {
            cmd = parseInsert();
        } else if (token.equalsIgnoreCase("SELECT")) {
            cmd = parseSelect();
        } else if (token.equalsIgnoreCase("UPDATE")) {
            cmd = parseUpdate();
//        } else if (token.equalsIgnoreCase("ALTER")) {
//            cmd = parseAlter();
        } else if (token.equalsIgnoreCase("DELETE")) {
            cmd = parseDelete();
        } else if (token.equalsIgnoreCase("DROP")) {
            cmd = parseDrop();
//        } else if (token.equalsIgnoreCase("JOIN")) {
//            cmd = parseJoin();
        } else {
            return new parseFailCMD("[ERROR]" + " invalid command: " + token);
        }
        return cmd;
    }

    private DeleteCMD parseDelete() throws ParseException {
        DeleteCMD cmd = new DeleteCMD();

        if (!getNextToken().equalsIgnoreCase("DELETE")) {
            cmd.setError("[ERROR] Expected DELETE keyword");
            return cmd;
        }

        if (!getNextToken().equalsIgnoreCase("FROM")) {
            cmd.setError("[ERROR] Expected FROM keyword");
            return cmd;
        }

        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            cmd.setError("[ERROR] invalid table name");
            return cmd;
        }
        cmd.setTableName(tableName);

        if (getNextToken().equalsIgnoreCase("WHERE")) {
            try {
                cmd.setCondition(parseCondition());
            } catch (ParseException e) {
                cmd.setError("[ERROR] invalid condition provided");
                return cmd;
            }
        } else {
            cmd.setError("[ERROR] expected WHERE keyword");
            return cmd;
        }

        if (!getNextToken().equals(";")) {
            cmd.setError("[ERROR] Expected semicolon");
            return cmd;
        }
        return cmd;
    }

    private UpdateCMD parseUpdate() throws ParseException {
        UpdateCMD cmd = new UpdateCMD();

        if (!getNextToken().equalsIgnoreCase("UPDATE")) {
            cmd.setError("[ERROR] Expected UPDATE keyword");
            return cmd;
        }

        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            cmd.setError("[ERROR] Invalid table name");
            return cmd;
        }
        cmd.setTableName(tableName);

        if (!getNextToken().equalsIgnoreCase("SET")) {
            cmd.setError("[ERROR] Expected SET keyword");
            return cmd;
        }

        // Parse set clause list
        try {
            cmd.setSetClauseList(parseSetClauseList());
        } catch (ParseException e) {
            cmd.setError("[ERROR] Invalid input");
            return cmd;
        }

        if (getNextToken().equalsIgnoreCase("WHERE")) {
            cmd.setWhereQuery(true);
            try {
                cmd.setCondition(parseCondition());
            } catch (ParseException e) {
                cmd.setError("[ERROR] Invalid condition");
                return cmd;
            }
        } else {
            pos--;
        }

        if (!getNextToken().equals(";")) {
            cmd.setError("[ERROR] Expected semicolon");
            return cmd;
        }
        return cmd;
    }

    private List<SetClause> parseSetClauseList() throws ParseException {
        List<SetClause> setClauseList = new ArrayList<>();

        // Parse first set clause
        SetClause setClause = parseSetClause();
        setClauseList.add(setClause);

        // Parse remaining set clauses
        while (getNextToken().equals(",")) {
            setClause = parseSetClause();
            setClauseList.add(setClause);
        }
        pos--;

        return setClauseList;
    }

    private SetClause parseSetClause() throws ParseException {

        // Parse attribute name
        String attributeName = getNextToken();
        if (!attributeName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            throw new ParseException("[ERROR] Invalid attribute name", pos);
        }

        if (!getNextToken().equals("=")) {
            throw new ParseException("[ERROR] Expected \"=\" operator", pos);
        }

        // Parse the value
        DataValue value = new DataValue(parseDataValue(), attributeName);

        return new SetClause(attributeName, value);
    }

    private String parseDataValue() throws ParseException {
        String token = getNextToken();

        // Check if the token is a quoted string
        if (token.startsWith("\"") && token.endsWith("\"")) {
            return token.substring(1, token.length() - 1);
        }

        // Check if the token is a number
        try {
            Double.parseDouble(token);
            return token;
        } catch (NumberFormatException e) {}

        // Check if the token is a boolean
        if (token.equalsIgnoreCase("TRUE") || token.equalsIgnoreCase("FALSE")) {
            return token;
        }

        // If none of the above, it's an invalid data value
        throw new ParseException("[ERROR] Invalid data value", pos);
    }


    private DropCMD parseDrop() throws ParseException {
        DropCMD cmd = new DropCMD();

        String token = getNextToken();
        if (!token.equals("DROP")) {
            cmd.setError("[ERROR] Expected 'DROP' but got '" + token + "'");
            return cmd;
        }

        token = getNextToken();
        if (!token.equals("DATABASE") && !token.equals("TABLE")) {
            cmd.setError("[ERROR] Expected 'DATABASE' or 'TABLE' but got '" + token + "'");
            return cmd;
        }
        boolean isDatabase = token.equals("DATABASE");

        if (isDatabase) {
            // Verify the next token is the name of the database to be dropped
            String databaseName = getNextToken();
            if (databaseName.isEmpty()) {
                cmd.setError("[ERROR] Expected a database name but got an empty string");
                return cmd;
            }
            cmd.setDatabaseName(databaseName);
        } else {
            // Verify the next token is the name of the table to be dropped
            String tableName = getNextToken();
            if (tableName.isEmpty()) {
                cmd.setError("[ERROR] Expected a table name but got an empty string");
                return cmd;
            }
            cmd.setTableName(tableName);
        }

        token = getNextToken();
        if (!token.equals(";")) {
            cmd.setError("[ERROR] Expected ';' but got '" + token + "'");
            return cmd;
        }

        return cmd;
    }


    private SelectCMD parseSelect() throws ParseException {
        SelectCMD cmd = new SelectCMD();

        if (!getNextToken().equalsIgnoreCase("SELECT")) {
            cmd.setError("[ERROR] Expected SELECT keyword");
            return cmd;
        }

        // Parse attribute list
        if (getNextToken().equals("*")) {
            cmd.addAttribute("*");
        } else {
            pos--;
            try {
                cmd.setAttributeList(parseAttListSelect());
            } catch (ParseException e) {
                cmd.setError("[ERROR] Invalid input");
                return cmd;
            }
        }

        if (!getNextToken().equalsIgnoreCase("FROM")) {
            cmd.setError("[ERROR] Expected FROM keyword");
            return cmd;
        }

        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            System.out.println("INSIDE");
            cmd.setError("[ERROR] invalid table name");
            return cmd;
        }
        cmd.setTableName(tableName);

        // Check if there's a WHERE clause
        if (getNextToken().equalsIgnoreCase("WHERE")) {
            cmd.setWhereQuery(true);
            try {
                cmd.setCondition(parseCondition());
            } catch (ParseException e) {
                e.printStackTrace();
                cmd.setError("[ERROR] invalid table name");
                return cmd;
            }
        } else {
            pos--;
        }

        if (!getNextToken().equals(";")) {
            cmd.setError("[ERROR] Expected semicolon");
            return cmd;
        }
        return cmd;
    }

    private List<String> parseAttListSelect() throws ParseException {
        List<String> attributeList = new ArrayList<>();
        String attributeName = getNextToken();
        if (!attributeName.matches("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)?")) {
            throw new ParseException("Invalid attribute name: " + attributeName, pos);
        }
        attributeList.add(attributeName);
        while (true) {
            String nextToken = getNextToken();
            if (nextToken.equals("FROM")) {
                --pos;
                break;
            }
            if (nextToken.equals(";")) {
                break;
            } else if (nextToken.equals(",")) {
                attributeName = getNextToken().trim();
                if (!attributeName.matches("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)?")) {
                    throw new ParseException("Invalid attribute name: " + attributeName, pos);
                }
                attributeList.add(attributeName);
            } else if (nextToken.matches("=|>|<|>=|<=|!=|LIKE")) {
                attributeList.add(nextToken);
            } else {
                pos--;
                throw new ParseException("Expected comma or semicolon between attribute names", pos);
            }
        }
        return attributeList;
    }

    private Condition parseCondition() throws ParseException {
        // Check if the condition is a simple attribute-value comparison
        String firstToken = getNextToken();
        if (firstToken.matches("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)?")) {
            String secondToken = getCurrentToken();
            pos++;
            if (secondToken.equals("==") || secondToken.equals(">") || secondToken.equals("<") ||
                    secondToken.equals(">=") || secondToken.equals("<=") || secondToken.equals("!=") ||
                    secondToken.equalsIgnoreCase("LIKE")) {
                String thirdToken = getNextToken();
                if (thirdToken.matches("('.*')|TRUE|FALSE|-?[0-9]+(\\.[0-9]*)?")) {
                    return new Condition(firstToken, secondToken, thirdToken);
                }
            }
        }

        // Check if the condition is a nested condition
        if (firstToken.equals("(")) {
            Condition nestedCondition = parseCondition();
            // Parse boolean operator and right-hand side of the condition
            String boolOp = getNextToken();
            if (boolOp.equalsIgnoreCase("AND") || boolOp.equalsIgnoreCase("OR")) {
                Condition rightCondition = parseCondition();
                // Consume the closing parenthesis
                if (!getNextToken().equals(")")) {
                    throw new ParseException("Expected closing parenthesis", pos);
                }
                return new Condition(nestedCondition, boolOp, rightCondition);
            } else {
                throw new ParseException("Invalid boolean operator", pos);
            }
        } else {
            throw new ParseException("Invalid condition", pos);
        }
    }

    private InsertCMD parseInsert() throws ParseException {
        InsertCMD cmd = new InsertCMD(null, null);

        if (!getNextToken().equalsIgnoreCase("INSERT") || !getNextToken().equalsIgnoreCase("INTO")) {
            cmd.setError("[ERROR] Expected INSERT INTO keywords");
            return cmd;
        }

        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            cmd.setError("[ERROR] invalid table name");
            return cmd;
        }

        if (!getNextToken().equalsIgnoreCase("VALUES") || !getNextToken().equals("(")) {
            cmd.setError("[ERROR] Expected VALUES keyword and opening parenthesis");
            return cmd;
        }

        try {
            // Parse the list of values
            List<String> values = parseValueList();

            if (!getNextToken().equals(")") || !getNextToken().equals(";")) {
                cmd.setError("[ERROR] Expected closing parenthesis and semicolon");
                return cmd;
            }

            return new InsertCMD(tableName, values);
        } catch (ParseException e) {
            cmd.setError("[ERROR] invalid input");
            return cmd;
        }
    }

    private List<String> parseValueList() throws ParseException {
        List<String> values = new ArrayList<>();
        while (true) {
            String value = parseValue();

            values.add(value);
            // Check if there are more values
            String nextToken = getNextToken();
            if (nextToken.equals(")")) {
                pos--;
                break;
            } else if (!nextToken.equals(",")) {
                throw new ParseException("Expected comma or closing parenthesis in value list", pos);
            }
        }
        return values;
    }

    private String parseValue() throws ParseException {
        String token = getNextToken();
        if (token.equalsIgnoreCase("NULL")) {
            return "NULL";
        } else if (token.equalsIgnoreCase("TRUE")) {
            return "TRUE";
        } else if (token.equalsIgnoreCase("FALSE")) {
            return "FALSE";
        } else if (token.startsWith("'")) {
            // String literal
            if (token.endsWith("'")) {
                // String literal with no embedded quotes
                return token.substring(1, token.length() - 1);
            } else {
                // String literal with embedded quotes
                StringBuilder sb = new StringBuilder();
                sb.append(token.substring(1));
                while (true) {
                    String nextToken = getNextToken();
                    sb.append(" ");
                    sb.append(nextToken);
                    if (nextToken.endsWith("'")) {
                        return sb.toString().substring(0, sb.length() - 1);
                    }
                }
            }
        } else {
            return token;
        }
    }

    private CreateCMD parseCreate () throws ParseException {
        CreateCMD cmd = new CreateCMD(null, null, false);

        if (!getNextToken().equalsIgnoreCase("CREATE")) {
            cmd.setError("[ERROR] expected CREATE keyword\"");
            return cmd;
        }
        // Determine if this is a create database or create table command
        String nextToken = getCurrentToken();
        if (nextToken.equalsIgnoreCase("DATABASE")) {

            getNextToken();

            String databaseName = null;
            nextToken = getNextToken();
            if (nextToken.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                databaseName = nextToken;
            } else {
                pos--;
                cmd.setError("[ERROR] Expected database name");
                return cmd;
            }

            if (!getNextToken().equals(";")) {
                cmd.setError("[ERROR] Expected ; at end of command");
                return cmd;
            }
            return new CreateCMD(databaseName, null, true);
        } else if (nextToken.equalsIgnoreCase("TABLE")) {
            getNextToken();

            String tableName = getNextToken();
            if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                cmd.setError("[ERROR] Invalid table name\"");
                return cmd;
            }

            // Determine if there is an attribute list specified
            nextToken = getNextToken();
            if (!nextToken.equals("(")) {
                pos--;
            } else {
                List<String> attributes = parseAttributeList();

                if (!getNextToken().equals(")")) {
                    throw new ParseException("Expected ) at end of attribute list", pos);
                }
                return new CreateCMD(tableName, attributes, false);
            }
            if (!getNextToken().equals(";")) {
                cmd.setError("[ERROR] Expected ; at end of command");
                return cmd;
            }
            return new CreateCMD(tableName, null, false);
        } else {
            cmd.setError("[ERROR] Expected DATABASE or TABLE keyword after CREATE");
            return cmd;
        }
    }

    private List<String> parseAttributeList() throws ParseException {
        List<String> attributes = new ArrayList<>();

        String attributeName = parseAttributeName();
        attributes.add(attributeName);

        // Parse any subsequent attribute names separated by commas
        String nextToken = getNextToken();
        while (nextToken.equals(",")) {
            attributeName = parseAttributeName();
            attributes.add(attributeName);
            nextToken = getNextToken();
        }

        pos--;
        return attributes;
    }

    private String parseAttributeName() throws ParseException {
        String attributeName = getNextToken();

        // Check if the attribute name has a table prefix
        String nextToken = getNextToken();
        if (nextToken.equals(".")) {
            String tableName = attributeName;
            attributeName = getNextToken();

            // Construct the fully qualified attribute name
            attributeName = tableName + "." + attributeName;
        } else {
            pos--;
        }
        return attributeName;
    }


    private UseCMD parseUse() throws ParseException {
        UseCMD cmd = new UseCMD(null);

        if (!getNextToken().equalsIgnoreCase("USE")) {
            cmd.setError("[ERROR] Expected USE keyword");
            return cmd;
        }

        String databaseName = null;
        String nextToken = getNextToken();
        if (nextToken.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            databaseName = nextToken;
        } else {
            cmd.setError("[ERROR] Expected a database name");
            return cmd;
        }

         if (!getNextToken().equalsIgnoreCase(";")) {
             cmd.setError("[ERROR] Expected ; at end of command");
             return cmd;
        }

        return new UseCMD(databaseName);
    }

//    private AlterCMD parseAlter() throws ParseException {
//        AlterCMD cmd = new AlterCMD();
//        // Parse the rest of the command here
//        // Set the attributes of the AlterCMD object based on the tokens
//        return cmd;
//    }

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



