import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Message {

    private static final String WELCOME_MESSAGE = "Welcome to QuickChat";
    private static final String COMING_SOON = "Feature is still in development. Coming Soon!";
    private static final int MAX_MSG_TEXT_LENGTH = 250;
    private static final int MAX_RECIPIENT_LENGTH = 10;
    private static final Random RANDOM = new Random();

    // Stores all messages that are sent or stored
    public static final List<Message> messageList = new ArrayList<>();

    private String messageText;
    private String recipient;
    private String messageID;
    private String messageHash;
    private int numMessagesSent;

    public Message(String text, String recipient, int count) {
        this.messageText = text;
        this.recipient = recipient;
        this.numMessagesSent = count;

        this.messageID = generateUniqueMessageID();
        this.messageHash = createMessageHash();
    }

    public Message() {
        // Default constructor
    }

    // PUBLIC ACCESSOR FOR TESTING (Likely the missing method)
    public static List<Message> getMessageList() {
        return messageList;
    }

    public static void runQuickChatApp() {
        Scanner scanner = new Scanner(System.in);
        // ... (runQuickChatApp logic remains the same)
        System.out.println("\n==================================");
        System.out.println(WELCOME_MESSAGE);
        System.out.println("==================================");

        System.out.println("Please define the maximum number of messages you wish to send: ");
        int maxMessagesLimit = 0;
        try {
            maxMessagesLimit = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Setting message limit to 5 by default.");
            scanner.nextLine();
            maxMessagesLimit = 5;
        }

        int choice = 0;

        while (choice != 3) {

            int currentMessagesSent = returnTotalMessagess();

            if (currentMessagesSent >= maxMessagesLimit) {
                System.out.println("\n--- Message Limit Reached ---");
                System.out.println("The maximum limit of " + maxMessagesLimit + " messages has been sent.");
                System.out.println("Total successfully sent/stored messages: " + currentMessagesSent);
                displayMenuLimited(currentMessagesSent);
            } else {
                displayMenu(currentMessagesSent);
            }

            System.out.println("Enter your choice (1-3): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        if (currentMessagesSent < maxMessagesLimit) {
                            handleMessageCreation(scanner);
                        } else {
                            System.out.println("\n Message limit reached. Please choose option 2 or 3.");
                        }
                        break;
                    case 2:
                        System.out.println(printMessages());
                        break;
                    case 3:
                        if (currentMessagesSent < maxMessagesLimit) {
                            System.out.println("\n--- Session Summary ---");
                            System.out.println("You set a limit of " + maxMessagesLimit + " messages.");
                            System.out.println("Total successfully sent/stored messages: " + currentMessagesSent);
                        }
                        System.out.println("\nThank you for using QuickChat. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1, 2, or 3.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }


    public boolean checkMessageID() {
        // This method ensures that the message ID is not more than ten characters.
        return this.messageID != null && this.messageID.length() <= 10;
    }

    // Int: checkRecipientCell()
    public int checkRecipientCell() {
        if (this.recipient == null) return 0;

        // ensures that the recipient cell number is no more than ten characters long
        if (this.recipient.length() > MAX_RECIPIENT_LENGTH) {
            return 0;
        }

        // and starts with. (interpreted as starts with '+')
        if (!this.recipient.startsWith("+")) {
            return 0;
        }
        return 1;
    }

    public String createMessageHash() {
        if (this.messageID == null || this.messageText == null) return "ERROR: DATA MISSING";

        String idPrefix = this.messageID.substring(0, Math.min(2, this.messageID.length()));
        String msgCount = String.valueOf(this.numMessagesSent);

        String[] words = this.messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        // Strip punctuation/symbols to match the hash format (e.g., HITHANKS, HIPAYMENT)
        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        // Format: ID_PREFIX:COUNT:FIRSTWORDLASTWORD
        return String.format("%s:%s:%s%s",
                idPrefix,
                msgCount,
                firstWord.toUpperCase(),
                lastWord.toUpperCase());
    }

    // String:SentMessage()
    public String SentMessage(Scanner scanner) {
        int subChoice = 0;

        // The full details of each message should be displayed on the screen (using JOptionPane)
        displayMessageDetails(this);

        while (true) {
            System.out.println("\n--- Send Message Options ---");
            // should allow the user to choose if they want to send, store, or disregard the message.
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later (JSON)");
            System.out.println("Choose an action (1-3): ");

            if (scanner.hasNextInt()) {
                subChoice = scanner.nextInt();
                scanner.nextLine();

                switch (subChoice) {
                    case 1:
                        messageList.add(this);
                        return "SEND";
                    case 2:
                        return "DISREGARD";
                    case 3:
                        storeMessage(this);
                        messageList.add(this);
                        return "STORE";
                    default:
                        System.out.println("Invalid option. Please choose 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                subChoice = 0;
            }
        }
    }

    // String: printMessages()
    public static String printMessages() {
        if (messageList.isEmpty()) {
            return "\n--- Sent/Stored Message History ---\nNo messages sent or stored yet.\n----------------------------------\n";
        }

        StringBuilder sb = new StringBuilder("\n--- Sent/Stored Message History ---\n");
        for (Message msg : messageList) {
            sb.append(String.format(
                    "[%d] ID: %s | Recipient: %s | Hash: %s\n",
                    msg.numMessagesSent, msg.messageID, msg.recipient, msg.messageHash
            ));
        }
        sb.append("-------------------------------------------\n");
        return sb.toString();
    }

    // Int: returnTotalMessagess()
    public static int returnTotalMessagess() {
        // This method returns the total number of messages sent.
        return messageList.size();
    }

    // Helper for testing
    public static void clearMessageListForTesting() {
        messageList.clear();
    }

    // Your own defined storeMessage() method
    private static void storeMessage(Message msg) {
        String fileName = "message_" + msg.messageID + ".json";

        String jsonContent = String.format(
                "{\n  \"MessageID\": \"%s\",\n  \"NumSent\": %d,\n  \"Recipient\": \"%s\",\n  \"Message\": \"%s\",\n  \"Hash\": \"%s\"\n}",
                msg.messageID, msg.numMessagesSent, msg.recipient, msg.messageText, msg.messageHash
        );

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonContent);
            System.out.println("File saved successfully to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the JSON file: " + e.getMessage());
        }
    }

    private static void handleMessageCreation(Scanner scanner) {

        String recipient = getValidatedRecipient(scanner);

        String messageText = getValidatedMessageText(scanner);

        int newCount = returnTotalMessagess() + 1;

        Message currentMessage = new Message(messageText, recipient, newCount);

        String action = currentMessage.SentMessage(scanner);

        if (action.equals("SEND")) {
            System.out.println(" Message " + newCount + " sent successfully!");
        } else if (action.equals("STORE")) {
            System.out.println(" Message " + newCount + " stored successfully!");
        } else {
            System.out.println(" Message entry canceled.");
        }
    }

    private static String getValidatedRecipient(Scanner scanner) {
        String recipient;
        while (true) {
            System.out.println("\nEnter Recipient Cell Number (e.g., +27123456789): ");
            recipient = scanner.nextLine().trim();
            if (recipient.startsWith("+") && recipient.length() <= MAX_RECIPIENT_LENGTH) {
                return recipient;
            } else {
                System.out.println(" Invalid Recipient. Must contain an international code (starting with '+') and be no more than " + MAX_RECIPIENT_LENGTH + " characters long.");
            }
        }
    }

    private static String getValidatedMessageText(Scanner scanner) {
        String message;
        while (true) {
            System.out.println("Enter your Message (Max " + MAX_MSG_TEXT_LENGTH + " characters): ");
            message = scanner.nextLine();

            if (message.length() > MAX_MSG_TEXT_LENGTH) {
                System.out.println("Please enter a message of less than 50 characters.");
            } else {
                System.out.println("Message sent");
                return message;
            }
        }
    }

    private static String generateUniqueMessageID() {
        // This is a randomly generated ten-digit number that is stored for each correlating message
        long nineDigitBase = 100_000_000L + RANDOM.nextInt(900_000_000);
        return String.valueOf(nineDigitBase) + RANDOM.nextInt(10);
    }

    private static void displayMessageDetails(Message msg) {
        String details = String.format(
                """
                        Message ID: %s
                        Message Hash: %s
                        Recipient: %s
                        Message: %s
                        """,
                msg.messageID,
                msg.messageHash,
                msg.recipient,
                msg.messageText
        );
        JOptionPane.showMessageDialog(null, details, "Message Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void displayMenu(int currentCount) {
        System.out.println("\n--- QuickChat Menu (Sent: " + currentCount + ") ---");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    private static void displayMenuLimited(int currentCount) {
        System.out.println("\n--- QuickChat Menu (LIMIT REACHED) ---");
        System.out.println("1) Send Messages (Disabled)");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    // Public getters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public int getNumMessagesSent() { return numMessagesSent; }
}