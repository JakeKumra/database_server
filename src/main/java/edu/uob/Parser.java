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
//        } else if (token.equalsIgnoreCase("UPDATE")) {
//            cmd = parseUpdate();
//        } else if (token.equalsIgnoreCase("ALTER")) {
//            cmd = parseAlter();
//        } else if (token.equalsIgnoreCase("DELETE")) {
//            cmd = parseDelete();
        } else if (token.equalsIgnoreCase("DROP")) {
            cmd = parseDrop();
//        } else if (token.equalsIgnoreCase("JOIN")) {
//            cmd = parseJoin();
        } else {
            return new parseFailCMD("[ERROR]" + " invalid command: " + token);
        }
        return cmd;
    }
//
//    private UpdateCMD parseUpdate() throws ParseException {
//        UpdateCMD cmd = new UpdateCMD();
//        String token = getNextToken();
//        if (token.equalsIgnoreCase("UPDATE")) {
//            cmd.setTableName(getNextToken());
//            token = getNextToken();
//            if (!token.equalsIgnoreCase("SET")) {
//                throw new ParseException("Expected SET after table name");
//            }
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            token = getNextToken();
//            while (!token.equalsIgnoreCase("WHERE")) {
//                String attributeName = token;
//                token = getNextToken();
//                if (!token.equals("=")) {
//                    throw new ParseException("Expected = after attribute name");
//                }
//                Value value = parseValue();
//                nameValuePairs.add(new NameValuePair(attributeName, value));
//                token = getNextToken();
//                if (token.equals(",")) {
//                    token = getNextToken();
//                } else if (!token.equalsIgnoreCase("WHERE")) {
//                    throw new ParseException("Expected , or WHERE after name-value pair");
//                }
//            }
//            cmd.setNameValuePairs(nameValuePairs);
//            Condition condition = parseCondition();
//            cmd.setCondition(condition);
//        } else {
//            throw new ParseException("Expected UPDATE command");
//        }
//        return cmd;
//    }

    private DropCMD parseDrop() throws ParseException {
        DropCMD cmd = new DropCMD();

        // Verify the first token is "DROP"
        String token = getNextToken();
        if (!token.equals("DROP")) {
            cmd.setParseError();
            cmd.setErrorMessage("[ERROR] Expected 'DROP' but got '" + token + "'");
            return cmd;
        }

        // Verify the second token is "DATABASE" or "TABLE"
        token = getNextToken();
        if (!token.equals("DATABASE") && !token.equals("TABLE")) {
            cmd.setParseError();
            cmd.setErrorMessage("[ERROR] Expected 'DATABASE' or 'TABLE' but got '" + token + "'");
            return cmd;
        }
        boolean isDatabase = token.equals("DATABASE");

        // Create a new DropCMD object and set its attributes
        if (isDatabase) {
            // Verify the next token is the name of the database to be dropped
            String databaseName = getNextToken();
            if (databaseName.isEmpty()) {
                cmd.setParseError();
                cmd.setErrorMessage("[ERROR] Expected a database name but got an empty string");
                return cmd;
            }
            cmd.setDatabaseName(databaseName);
        } else {
            // Verify the next token is the name of the table to be dropped
            String tableName = getNextToken();
            if (tableName.isEmpty()) {
                cmd.setParseError();
                cmd.setErrorMessage("[ERROR] Expected a table name but got an empty string");
                return cmd;
            }
            cmd.setTableName(tableName);
        }

        // Verify the final token is ";"
        token = getNextToken();
        if (!token.equals(";")) {
            cmd.setParseError();
            cmd.setErrorMessage("[ERROR] Expected ';' but got '" + token + "'");
            return cmd;
        }

        return cmd;
    }


    private SelectCMD parseSelect() throws ParseException {
        SelectCMD cmd = new SelectCMD();

        // Consume the "SELECT" keyword
        if (!getNextToken().equalsIgnoreCase("SELECT")) {
            throw new ParseException("Expected SELECT keyword", pos);
        }

        // Parse attribute list
        if (getNextToken().equals("*")) {
            cmd.addAttribute("*");
        } else {
            pos--;
            cmd.setAttributeList(parseAttListSelect());
        }

        // Consume the "FROM" keyword
        if (!getNextToken().equalsIgnoreCase("FROM")) {
            throw new ParseException("Expected FROM keyword", pos);
        }

        // Get table name
        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            throw new ParseException("Invalid table name", pos);
        }
        cmd.setTableName(tableName);

        // Check if there's a WHERE clause
        if (getNextToken().equalsIgnoreCase("WHERE")) {
            cmd.setWhereQuery(true);
            cmd.setCondition(parseCondition());
        } else {
            pos--;
        }

        // Consume the semicolon
        if (!getNextToken().equals(";")) {
            throw new ParseException("Expected semicolon", pos);
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

        // Consume the "INSERT INTO" keywords
        if (!getNextToken().equalsIgnoreCase("INSERT") || !getNextToken().equalsIgnoreCase("INTO")) {
            throw new ParseException("Expected INSERT INTO keywords", pos);
        }

        // Get the table name
        String tableName = getNextToken();
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            throw new ParseException("Invalid table name", pos);
        }

        // Consume the "VALUES" keyword and the opening parenthesis
        if (!getNextToken().equalsIgnoreCase("VALUES") || !getNextToken().equals("(")) {
            throw new ParseException("Expected VALUES keyword and opening parenthesis", pos);
        }

        // Parse the list of values
        List<String> values = parseValueList();

        // Consume the closing parenthesis and semicolon
        if (!getNextToken().equals(")") || !getNextToken().equals(";")) {
            throw new ParseException("Expected closing parenthesis and semicolon", pos);
        }

        // Create and return the InsertCMD object
        return new InsertCMD(tableName, values);
    }

    private List<String> parseValueList() throws ParseException {
        List<String> values = new ArrayList<>();

        while (true) {
            String value = parseValue();

            // Add the value to the list
            values.add(value);

            // Check if there are more values
            String nextToken = getNextToken();
            if (nextToken.equals(")")) {
                // End of value list
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
            // Numeric literal
            return token;
        }
    }

    private CreateCMD parseCreate () throws ParseException {

        // Consume the "CREATE" keyword
        if (!getNextToken().equalsIgnoreCase("CREATE")) {
            throw new ParseException("Expected CREATE keyword", pos);
        }
        // Determine if this is a create database or create table command
        String nextToken = getCurrentToken();
        if (nextToken.equalsIgnoreCase("DATABASE")) {
            // Consume the "DATABASE" keyword
            getNextToken();
            // Consume the database name, if present
            String databaseName = null;
            nextToken = getNextToken();
            if (nextToken.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                databaseName = nextToken;
            } else {
                pos--;
                throw new ParseException("Expected database name", pos);
            }

            // Consume the ';' at the end of the command
            if (!getNextToken().equals(";")) {
                throw new ParseException("Expected ; at end of command", pos);
            }
            return new CreateCMD(databaseName, null, true);
        } else if (nextToken.equalsIgnoreCase("TABLE")) {
            getNextToken();

            // Consume the table name
            String tableName = getNextToken();
            if (!tableName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                throw new ParseException("Invalid table name", pos);
            }

            // Determine if there is an attribute list specified
            nextToken = getNextToken();
            if (!nextToken.equals("(")) {
                pos--;
                // inside else if there is an attribute list or code after
            } else {
                List<String> attributes = parseAttributeList();

                // Consume the ')' at the end of the attribute list
                if (!getNextToken().equals(")")) {
                    throw new ParseException("Expected ) at end of attribute list", pos);
                }

                return new CreateCMD(tableName, attributes, false);
            }

            // Consume the ';' at the end of the command
            if (!getNextToken().equals(";")) {
                throw new ParseException("Expected ; at end of command", pos);
            }

            // Create and return the CreateTableCMD object
            return new CreateCMD(tableName, null, false);
        } else {
            throw new ParseException("Expected DATABASE or TABLE keyword after CREATE", pos);
        }
    }

    private List<String> parseAttributeList() throws ParseException {
        List<String> attributes = new ArrayList<>();

        // Parse the first attribute name
        String attributeName = parseAttributeName();
        attributes.add(attributeName);

        // Parse any subsequent attribute names separated by commas
        String nextToken = getNextToken();
        while (nextToken.equals(",")) {
            attributeName = parseAttributeName();
            attributes.add(attributeName);
            nextToken = getNextToken();
        }

        // Return the list of attribute names
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



