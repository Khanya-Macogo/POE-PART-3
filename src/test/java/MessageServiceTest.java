import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

// Note: Ensure your Message.java class is available in the test classpath.

public class MessageServiceTest {

    // --- Dynamic variables to capture generated data for subsequent tests ---
    private String m3HashToFind;
    private String m4IDToFind;

    /**
     * Executes before each test method runs. This method is the JUnit 4 equivalent
     * of @BeforeEach in JUnit 5.
     */
    @Before
    public void setupTestData() {
        // Clear static lists before running a new test batch for isolation
        Message.messageList.clear();
        Message.messageIDs.clear();
        Message.messageHashes.clear();
        Message.disregardedMessages.clear();

        // Define fixed data for reliable testing outcomes
        String recipientA = "+27838884567";
        String recipientB = "+27123456789";

        // The constructor populates the static ID/Hash lists

        // Message 1 (Short)
        Message m1 = new Message("Did you get the cake?", recipientA, 1);
        Message.messageList.add(m1);

        // Message 2 (Medium)
        Message m2 = new Message("It is dinner time!", recipientA, 2);
        Message.messageList.add(m2);

        // Message 3 (Longest - Text to be deleted/longest message)
        Message m3 = new Message("Where are you? You are late! I have asked you to be on time.", recipientA, 3);
        Message.messageList.add(m3);
        m3HashToFind = m3.getMessageHash(); // Capture the dynamic hash for the delete test

        // Message 4 (Different Recipient - Message for ID search)
        Message m4 = new Message("Ok, I am leaving without you.", recipientB, 4);
        Message.messageList.add(m4);
        m4IDToFind = m4.getMessageID(); // Capture the dynamic ID for the search test
    }

    // --- TEST 1: Sent Messages array correctly populated (R1 Check) ---
    @Test
    public void test1_SentMessagesArrayCorrectlyPopulated() {
        // 1. Check core history list size
        assertEquals("The main messageList should contain 4 elements.", 4, Message.messageList.size());

        // 2. Check Requirement 1 static list sizes
        assertEquals("The messageIDs list should contain 4 IDs.", 4, Message.messageIDs.size());
        assertEquals("The messageHashes list should contain 4 Hashes.", 4, Message.messageHashes.size());

        // Verify a couple of messages are actually present
        assertTrue("Message 1 text not found.",
                Message.messageList.stream().anyMatch(m -> m.getMessageText().equals("Did you get the cake?")));
    }

    // --- TEST 2: Display the longest Message (R2.b) ---
    @Test
    public void test2_DisplayTheLongestMessage() {
        String actualResult = Message.displayLongestSentMessage();
        String expectedText = "Where are you? You are late! I have asked you to be on time.";

        assertTrue("The longest message was incorrectly identified.",
                actualResult.contains(expectedText));
    }

    // --- TEST 3: Search for message ID (R2.c) ---
    @Test
    public void test3_SearchForMessageID() {
        String actualResult = Message.searchMessageByID(m4IDToFind);

        assertTrue("Search result must contain the message text for M4.",
                actualResult.contains("Ok, I am leaving without you."));
        assertTrue("Search result must contain the recipient for M4.",
                actualResult.contains("+27123456789"));
        assertFalse("Message was incorrectly reported as not found.",
                actualResult.contains("not found"));
    }

    // --- TEST 4: Search all the messages sent or stored regarding a particular recipient (R2.d) ---
    // Example for mocking System.in for a Scanner-based method:
    @Test
    public void testMethodWithUserInput() {
        String simulatedInput = "input value\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // ACT: Call your method that uses Scanner(System.in)
        // ...

        // Clean up
        System.setIn(System.in);
    }

    // --- TEST 5: Delete a message using a message hash (R2.e) ---
    @Test
    public void test5_DeleteMessageByHash() {
        String actualResult = Message.deleteMessageByHash(m3HashToFind);

        assertEquals("List size should be 3 after deleting Message 3.", 3, Message.messageList.size());
        assertTrue("Confirmation message failed.", actualResult.contains("deleted successfully."));
    }

    // --- TEST 6: Display Report (R2.f) ---
    @Test
    public void test6_DisplayReport() {
        // Delete M3 to set the report state (3 messages remaining: M1, M2, M4)
        Message.deleteMessageByHash(m3HashToFind);

        String actualReport = Message.displayFullDetailsReport();

        // Check for remaining messages and absence of the deleted message
        assertTrue("Report missing M1 content.", actualReport.contains("Did you get the cake?"));
        assertFalse("Report should not contain the deleted hash.", actualReport.contains(m3HashToFind));
    }
}