package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

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

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test void testBasicUseQuery() {
        sendCommandToServer("CREATE DATABASE PeopleDB;");
        String responseTwo = sendCommandToServer("USE PeopleDB;");
        assertTrue(responseTwo.contains("[OK]"), "An attempt to USE existing database did not respond [OK]");
        String responseThree = sendCommandToServer("USE UnknownDb;");
        assertTrue(responseThree.contains("[ERROR]"), "Attempt to use non-existing database didn't return [ERROR]");
    }

    @Test
    public void testInvalidCreateCommands() {
        String invalidDbname = "SELECT";
        String responseOne = sendCommandToServer("CREATE DATABASE " + invalidDbname + ";");
        assertTrue(responseOne.contains("[ERROR]"), "A invalid query was made, however an [ERROR] tag was not returned");
        String validDbname = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + validDbname + ";");
        String responseTwo = sendCommandToServer("CREATE TABLE use (name, mark, pass);");
        assertTrue(responseTwo.contains("[ERROR]"), "A invalid query was made, however an [ERROR] tag was not returned");
        String responseThree = sendCommandToServer("CREATE TABLE marks (name, delete, pass);");
        assertTrue(responseThree.contains("[ERROR]"), "A invalid query was made, however an [ERROR] tag was not returned");
    }

    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Steve"), "An attempt was made to add Steve to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Clive"), "An attempt was made to add Clive to the table, but they were not returned by SELECT *");
    }

    @Test
    public void testBasicDelete() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Dave"), "A valid query was made, however an [OK] tag was not returned");
        sendCommandToServer("DELETE FROM marks WHERE name == 'Dave';");
        sendCommandToServer("DELETE FROM marks WHERE name == 'Clive';");
        String responseTwo = sendCommandToServer("SELECT * FROM marks;");
        assertFalse(responseTwo.contains("Dave"), "A valid delete query was made, however 'Dave' was returned");
        assertFalse(responseTwo.contains("Clive"), "A valid delete query was made, however 'Clive' was returned");
    }

    @Test
    public void testUpdateCMD() {
        String databaseName = "testUpdateDb";
        sendCommandToServer("CREATE DATABASE " + databaseName + ";");
        sendCommandToServer("USE " + databaseName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Clive';");
        String responseTwo = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(responseTwo.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseTwo.contains("38"), "A valid query was made, however 38 was not returned");
        assertFalse(responseTwo.contains("20"), "A valid query was made, however 20 was returned");
    }

    @Test
    public void testBasicCreateAndQueryFailures() {
        String randomName = generateRandomName();
        String responseOne = sendCommandToServer("USE " + randomName + ";");
        assertTrue(responseOne.contains("[ERROR]"), "An attempt to use a non-existent database didn't return [ERROR]");
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String responseTwo = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(responseTwo.contains("[ERROR]"), "Attempt to create existing database didn't return [ERROR]");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String responseThree = sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertTrue(responseThree.contains("[ERROR]"), "Attempt to create existing table didn't return [ERROR]");
        String responseFour = sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, FALSE);");
        assertTrue(responseFour.contains("[ERROR]"), "Attempt to insert too many values didn't return [ERROR]");
        String responseFive = sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65);");
        assertTrue(responseFive.contains("[ERROR]"), "Attempt to insert too few values didn't return [ERROR]");
        String responseSix = sendCommandToServer("CREATE TABLE students (name, age, gender, name);");
        assertTrue(responseSix.contains("[ERROR]"), "Duplicate column names didn't return [ERROR]");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that they work !)

    @Test
    public void testCreateParseFail() {
        String response = sendCommandToServer("CREATE TABLE;");
        System.out.println(response);
        assertTrue(response.contains("[ERROR]"), "CREATE TABLE with no name didn't return [ERROR]");
    }

    @Test
    public void testCreateAndInsert() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");

        sendCommandToServer("CREATE TABLE coursework (task, submission);");
        sendCommandToServer("INSERT INTO coursework VALUES (OXO, 3);");
        sendCommandToServer("INSERT INTO coursework VALUES (DB, 1);");
        sendCommandToServer("INSERT INTO coursework VALUES (OXO, 4);");
        sendCommandToServer("INSERT INTO coursework VALUES (STAG, 2);");

        String response = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("Bob"), "A valid query was made, however Bob was not returned");
        assertTrue(response.contains("Clive"), "A valid query was made, however Clive was not returned");
        assertTrue(response.contains("OXO"), "A valid query was made, however OXO was not returned");
        assertTrue(response.contains("Dave"), "A valid query was made, however Dave was not returned");
        assertTrue(response.contains("STAG"), "A valid query was made, however STAg was not returned");
    }

    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('John', 89, FALSE);");
        String responseOne = sendCommandToServer("SELECT name FROM marks;");
        assertTrue(responseOne.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        String responseTwo = sendCommandToServer("SELECT id FROM marks;");
        assertTrue(responseTwo.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        String responseThree = sendCommandToServer("SELECT id FROM marks WHERE name == 'Steve';");
        assertTrue(responseThree.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseThree.contains("1"), "A valid query was made, however an [OK] tag was not returned");
        String responseFour = sendCommandToServer("SELECT id FROM marks WHERE name == 'John';");
        assertTrue(responseFour.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseFour.contains("2"), "A valid query was made, however an [OK] tag was not returned");
        // Convert multi-lined responses into just a single line
        String singleLine = responseThree.replace("\n"," ").trim();
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

    @Test
    public void testAdvancedQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String responseOne = sendCommandToServer("SELECT * FROM marks WHERE name != 'Dave';");
        assertTrue(responseOne.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseOne.contains("Steve"), "A valid query was made, however 'Steve' was not returned");
        assertTrue(responseOne.contains("Bob"), "A valid query was made, however 'Bob' was not returned");
        assertTrue(responseOne.contains("Clive"), "A valid query was made, however 'Clive' was not returned");
        assertFalse(responseOne.contains("Dave"), "A valid query was made, however 'Dave' was returned");
        String responseTwo = sendCommandToServer("SELECT * FROM marks WHERE pass == TRUE;");
        assertTrue(responseTwo.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseTwo.contains("Steve"), "A valid query was made, however 'Steve' was not returned");
        assertTrue(responseTwo.contains("Dave"), "A valid query was made, however 'Dave' tag was not returned");
        assertFalse(responseTwo.contains("Clive"), "A valid query was made, however 'Clive' was returned'");
        assertFalse(responseTwo.contains("Bob"), "A valid query was made, however 'Bob' was returned");
    }

    @Test
    public void testAdvancedQueryIDTwo() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String responseOne = sendCommandToServer("SELECT * FROM marks WHERE mark > 35;");
        assertTrue(responseOne.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(responseOne.contains("Steve"), "A valid query was made, however Steve was not returned");
        assertTrue(responseOne.contains("Dave"), "A valid query was made, however Dave was not returned");
        assertFalse(responseOne.contains("Bob"), "A valid query was made, however Bob was returned");
        assertFalse(responseOne.contains("Clive"), "A valid query was made, however Clive was returned");
    }

    @Test
    public void testSelectParseFail() {
        String responseOne = sendCommandToServer("SELECT * FROM marks WHERE mark > 35;");
        assertTrue(responseOne.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String responseTwo = sendCommandToServer("SELECT * FROM; marks WHERE mark > 35");
        assertTrue(responseTwo.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        String responseThree = sendCommandToServer("SELECT * marks WHERE mark > 35");
        assertTrue(responseThree.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        String responseFour = sendCommandToServer("SELECT * marks WHERE mark > 35");
        assertTrue(responseFour.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        String responseFive = sendCommandToServer("SELECT * marks WHERE mark ! 35");
        assertTrue(responseFive.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        String responseSix = sendCommandToServer("SELECT * FROM;");
        assertTrue(responseSix.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
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

    @Test
    public void testDropParseError() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("DROP DTABASE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"), "Parsing error didn't return [ERROR]");
        String responseTwo = sendCommandToServer("DROP DTABASE " + randomName + "");
        assertTrue(responseTwo.contains("[ERROR]"), "Parsing error didn't return [ERROR]");
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

    @Test void testParseErrors() {
        String responseFour = sendCommandToServer("TEST PARSE ERROR");
        assertTrue(responseFour.contains("[ERROR]"), "Invalid command didn't return [ERROR]");
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("DROP DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        String responseTwo = sendCommandToServer("USE " + randomName + ";");
        assertTrue(responseTwo.contains("[ERROR]"), "Attempt to use non-existent table/database didn't return [ERROR]");
        String responseThree = sendCommandToServer("INSERT INTO " + randomName + " VALUES ('Steve', 65, TRUE);");
        assertTrue(responseThree.contains("[ERROR]"), "An attempt to insert into non-existing table didn't return [ERROR]");
        randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String responseFive = sendCommandToServer("DROP TABLE marks;");
        assertTrue(responseFive.contains("[OK]"), "An attempt to drop an existing table didn't return [OK]");
    }
}
