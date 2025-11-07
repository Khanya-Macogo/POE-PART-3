import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class MessageServiceTest {

    private String m3HashToFind;
    private String m4IDToFind;

    @Before
    public void setupTestData() {
        Message.messageList.clear();
        Message.messageIDs.clear();
        Message.messageHashes.clear();
        Message.disregardedMessages.clear();

        String recipientA = "+27838884567";
        String recipientB = "+27123456789";


        // Message 1 (Short)
        Message m1 = new Message("Did you get the cake?", recipientA, 1);
        Message.messageList.add(m1);

        // Message 2 (Medium)
        Message m2 = new Message("It is dinner time!", recipientA, 2);
        Message.messageList.add(m2);

        // Message 3 (Longest)
        Message m3 = new Message("Where are you? You are late! I have asked you to be on time.", recipientA, 3);
        Message.messageList.add(m3);
        m3HashToFind = m3.getMessageHash();

        // Message 4 (Different Recipient)
        Message m4 = new Message("Ok, I am leaving without you.", recipientB, 4);
        Message.messageList.add(m4);
        m4IDToFind = m4.getMessageID();
    }

    // --- TEST 1: Sent Messages array correctly populated ---
    @Test
    public void test1_SentMessagesArrayCorrectlyPopulated() {
        assertEquals("The main messageList should contain 4 elements.", 4, Message.messageList.size());

        assertEquals("The messageIDs list should contain 4 IDs.", 4, Message.messageIDs.size());
        assertEquals("The messageHashes list should contain 4 Hashes.", 4, Message.messageHashes.size());

        assertTrue("Message 1 text not found.",
                Message.messageList.stream().anyMatch(m -> m.getMessageText().equals("Did you get the cake?")));
    }

    // --- TEST 2: Display the longest Message ---
    @Test
    public void test2_DisplayTheLongestMessage() {
        String actualResult = Message.displayLongestSentMessage();
        String expectedText = "Where are you? You are late! I have asked you to be on time.";

        assertTrue("The longest message was incorrectly identified.",
                actualResult.contains(expectedText));
    }

    // --- TEST 3: Search for message ID---
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

    @Test
    public void testMethodWithUserInput() {
        String simulatedInput = "input value\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        System.setIn(System.in);
    }

    // --- TEST 5: Delete a message using a message hash---
    @Test
    public void test5_DeleteMessageByHash() {
        String actualResult = Message.deleteMessageByHash(m3HashToFind);

        assertEquals("List size should be 3 after deleting Message 3.", 3, Message.messageList.size());
        assertTrue("Confirmation message failed.", actualResult.contains("deleted successfully."));
    }

    // --- TEST 6: Display Report ---
    @Test
    public void test6_DisplayReport() {
        Message.deleteMessageByHash(m3HashToFind);

        String actualReport = Message.displayFullDetailsReport();

        assertTrue("Report missing M1 content.", actualReport.contains("Did you get the cake?"));
        assertFalse("Report should not contain the deleted hash.", actualReport.contains(m3HashToFind));
    }
}