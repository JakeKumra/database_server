package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleDBTests {

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName()
    {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testTokenizer() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ";
        Lexer instanceLexer = new Lexer(query);
        ArrayList<String> tokens = instanceLexer.tokenizeInput();
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    @Test
    public void testGetDatabaseFromFile() {
        // TODO fix below and include robust testing here later on
        // when this function is called it should create a new database and then test the function
        // but for now I will hardcode it

        Database testDb = server.getDatabaseFromFile("PeopleDB");
        assertNotNull(testDb);

        Table peopleTable = testDb.getTable("people");
        System.out.println(peopleTable.convertTableToString());

        Table studentsTable = testDb.getTable("students");
        System.out.println(studentsTable.convertTableToString());
    }
    //public Database getDatabaseFromFile(String dbName)

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test void testBasicUseQuery() {
        // TODO this will need to be dynamic so we need to first create a database and THEN test use
        // for now I am just hard coding it but this will need updating later on
        String response = sendCommandToServer("USE PeopleDB;");
        assertTrue(response.contains("[OK]"), "An attempt to USE existing database did not respond [OK]");
        String responseTwo = sendCommandToServer("USE UnknownDb;");
        assertTrue(responseTwo.contains("[ERROR]"), "Attempt to use non-existing database didn't return [ERROR]");
    }

    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
//        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
//        String responseTwo = sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        assertTrue(responseTwo.contains("[ERROR]"), "An invalid query was made, database " + randomName + "already exists");
//
//        String randomNameTwo = generateRandomName();
//        String responseThree = sendCommandToServer("CREATE TABLE " + randomNameTwo + ";");
//        System.out.println(responseThree);

        // TODO write test cases here
//        sendCommandToServer("USE PeopleDB;");
//        String randomNameTwo = generateRandomName();
//        String responseFour = sendCommandToServer("CREATE TABLE " + randomNameTwo + ";");
//        System.out.println(responseFour);
//        String responseFive = sendCommandToServer("CREATE TABLE " + randomNameTwo + "(name, mark, table.new, mike);");
//        System.out.println(responseFive);

        sendCommandToServer("CREATE DATABASE " + "exampleDb" + ";");
        sendCommandToServer("USE " + "exampleDb" + ";");
//         sendCommandToServer("CREATE TABLE " + "exampleTable" + ";");
        sendCommandToServer("CREATE TABLE exampleTable (name, mark, pass);");
        System.out.println(sendCommandToServer("INSERT INTO exampleTable VALUES ('Steve', 65, TRUE);"));
//        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
//        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
//        assertTrue(response.contains("Steve"), "An attempt was made to add Steve to the table, but they were not returned by SELECT *");
//        assertTrue(response.contains("Clive"), "An attempt was made to add Clive to the table, but they were not returned by SELECT *");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that they work !)

    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Steve';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Steve';` should have been an integer ID, but was " + lastToken);
        }
    }

    // A test to make sure that databases can be reopened after server restart
    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        // Create a new server object
        server = new DBServer();
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Steve"), "Steve was added to a table and the server restarted - but Steve was not returned by SELECT *");
    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
    @Test
    public void testForErrorTag() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        String response = sendCommandToServer("SELECT * FROM libraryfines;");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

}
