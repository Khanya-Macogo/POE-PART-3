
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;


public class MessageTest {
    //Message unit Test

    private Message messageContainer;

    @Before
    public void setup() {
        Message.clearMessageListForTesting();
    }

    @Test
    public void testTask1_MessageCreationAndHash() {
        int messageCount = 1;
        String recipient = "+27718693002";
        String messageText = "Hi Mike, can you join us for dinner tonight";

        Message msg1 = new Message(messageText, recipient, messageCount);
        assertEquals("Recipient check should return 0 due to length > 10.", 0, msg1.checkRecipientCell());

        String hash = msg1.getMessageHash();

        assertTrue("Hash should contain two parts separated by a colon", hash.contains(":"));
        String[] parts = hash.split(":");
        assertEquals("Hash should have 3 segments: Prefix, Count, Words", 3, parts.length);

        assertEquals("The second segment (count) should be 1", "1", parts[1]);
        assertEquals("The third segment (words) should be 'HITONIGHT'", "HITONIGHT", parts[2]);

        assertTrue("Message ID should be no more than 10 characters long.", msg1.getMessageID().length() <= 10);
    }

    @Test
    public void testTask1_ReturnTotalMessages_SelectSend() {
        Message msg1 = new Message("Test 1", "+2712345678", 1);
        Message.messageList.add(msg1);
        assertEquals("After Task 1 (Select Send), total messages should be 1.", 1, Message.returnTotalMessagess());
    }

    @Test
    public void testTask2_MessageCreationAndHash() {
        int messageCount = 1;
        String recipient = "08575975889";
        String messageText = "Hi Keegan, did you receive the payment?";
        Message msg2 = new Message(messageText, recipient, messageCount);
        assertEquals("Recipient check should return 0 (fail) because it's too long and missing '+'.", 0, msg2.checkRecipientCell());
        String hash = msg2.getMessageHash();

        assertTrue("Hash should contain two parts separated by a colon", hash.contains(":"));
        String[] parts = hash.split(":");

        assertEquals("The second segment (count) should be 1", "1", parts[1]);
        assertEquals("The third segment (words) should be 'HIPAYMENT'", "HIPAYMENT", parts[2]);
    }

    @Test
    public void testTask2_ReturnTotalMessages_SelectDiscard() {
        Message msg2 = new Message("Test 2", "08575975889", 1);
        assertEquals("After Task 2 (Select Discard), total messages should be 0 (list is clear).", 0, Message.returnTotalMessagess());
    }

    @Test
    public void testCheckMessageID_Valid() {
        // Message ID is auto-generated to 10 digits in the constructor
        Message msg = new Message("Text", "+123456789", 1);
        assertTrue("Auto-generated ID should pass validation.", msg.checkMessageID());
    }

    @Test
    public void testPrintMessages_Format() {
        // Arrange
        Message msg = new Message("Hello world", "+123456789", 1);
        Message.messageList.add(msg);
        String output = Message.printMessages();

        assertTrue("Output should contain the message ID.", output.contains(msg.getMessageID()));
        assertTrue("Output should contain the recipient.", output.contains(msg.getRecipient()));
        assertTrue("Output should contain the message hash.", output.contains(msg.getMessageHash()));
        assertTrue("Output should contain the message count.", output.contains("[1]"));
    }
}
