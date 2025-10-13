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
    private static final int MAX_RECIPIENT_LENGTH = 12;
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

    public static List<Message> getMessageList() {
        return messageList;
    }

    // --- REVISED: Integrated JOptionPane, Batch FOR loop, and correct increment ---
    public static void runQuickChatApp() {
        // Scanner is only kept for handling message details input (text, recipient)
        Scanner scanner = new Scanner(System.in);

        JOptionPane.showMessageDialog(null, WELCOME_MESSAGE, "Welcome to QuickChat", JOptionPane.INFORMATION_MESSAGE);

        // --- Get Max Message Limit (Still using Scanner/Console for initial setup) ---
        System.out.println("Please define the maximum number of messages you wish to send: ");
        int maxMessagesLimit = 0;
        try {
            maxMessagesLimit = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Setting message limit to 5 by default.");
            scanner.nextLine();
            maxMessagesLimit = 5;
        }

        int choice = 0;

        while (choice != 3) {
            int currentMessagesSent = returnTotalMessagess();
            String menuOptions;

            // --- Determine Menu Options based on limit ---
            if (currentMessagesSent >= maxMessagesLimit) {
                // Limit Reached Menu
                String limitReachedMsg = String.format(
                        "Message Limit Reached: %d/%d messages sent.\nPlease choose to show messages or quit.",
                        currentMessagesSent, maxMessagesLimit);

                JOptionPane.showMessageDialog(null, limitReachedMsg, "Limit Reached", JOptionPane.WARNING_MESSAGE);

                menuOptions = "1) Send Messages (Disabled)\n" +
                        "2) Show recently sent messages\n" +
                        "3) Quit";
            } else {
                // Standard Menu
                menuOptions = String.format("1) Send Messages (Sent: %d/%d)\n", currentMessagesSent, maxMessagesLimit) +
                        "2) Show recently sent messages\n" +
                        "3) Quit";
            }

            // --- Display Menu and Get Input using JOptionPane ---
            String input = JOptionPane.showInputDialog(
                    null,
                    menuOptions + "\n\nEnter your choice (1-3):",
                    "QuickChat Menu",
                    JOptionPane.QUESTION_MESSAGE
            );

            // --- Process Input ---
            if (input == null) {
                choice = 3;
            } else {
                try {
                    choice = Integer.parseInt(input.trim());

                    switch (choice) {
                        case 1:
                            if (currentMessagesSent < maxMessagesLimit) {
                                int remainingSlots = maxMessagesLimit - currentMessagesSent;

                                // Prompt for message batch size
                                String countInput = JOptionPane.showInputDialog(
                                        null,
                                        "You can send up to " + remainingSlots + " more messages.\n" +
                                                "How many messages would you like to send now (1-" + remainingSlots + ")?",
                                        "Message Batch Size",
                                        JOptionPane.QUESTION_MESSAGE
                                );

                                int numToSend = 0;
                                if (countInput != null) {
                                    try {
                                        numToSend = Integer.parseInt(countInput.trim());

                                        if (numToSend > remainingSlots || numToSend <= 0) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Invalid batch size. Must be between 1 and " + remainingSlots + ".",
                                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }

                                        // **THE FOR LOOP with CORRECT INCREMENT**
                                        for (int i = 0; i < numToSend; i++) {
                                            // Calculate the correct sequential message number:
                                            int uniqueNewCount = currentMessagesSent + (i + 1);

                                            // Console output for user guidance
                                            System.out.println("\n--- Entering Message " + (i + 1) + " of " + numToSend + " (Total Message No.: " + uniqueNewCount + ") ---");

                                            // Call the MODIFIED handler method with the unique count
                                            handleMessageCreation(scanner, uniqueNewCount);

                                            // Check if limit was hit mid-batch
                                            if (returnTotalMessagess() >= maxMessagesLimit) {
                                                System.out.println("Message limit reached during batch entry. Returning to main menu.");
                                                break;
                                            }
                                        }

                                    } catch (NumberFormatException e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid input for message count. Please enter a number.",
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }

                            } else {
                                JOptionPane.showMessageDialog(null, "Message limit reached. Please choose option 2 or 3.", "Limit Reached", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case 2:
                            // Display message history in a dialog
                            String history = printMessages();
                            JOptionPane.showMessageDialog(null, history, "Message History", JOptionPane.PLAIN_MESSAGE);
                            break;
                        case 3:
                            if (currentMessagesSent < maxMessagesLimit) {
                                String summary = String.format("--- Session Summary ---\nYou set a limit of %d messages.\nTotal successfully sent/stored messages: %d",
                                        maxMessagesLimit, currentMessagesSent);
                                JOptionPane.showMessageDialog(null, summary, "Session Summary", JOptionPane.INFORMATION_MESSAGE);
                            }
                            JOptionPane.showMessageDialog(null, "Thank you for using QuickChat. Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, or 3.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number (1, 2, or 3).", "Input Error", JOptionPane.ERROR_MESSAGE);
                    choice = 0;
                }
            }
        }

        scanner.close();
    }


    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }

    public int checkRecipientCell() {
        if (this.recipient == null) return 0;

        if (this.recipient.length() > MAX_RECIPIENT_LENGTH) {
            return 0;
        }

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

        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        // Format: ID_PREFIX:COUNT:FIRSTWORDLASTWORD
        return String.format("%s:%s:%s%s",
                idPrefix,
                msgCount,
                firstWord.toUpperCase(),
                lastWord.toUpperCase());
    }

    // --- REVISED: Uses JOptionPane buttons instead of Scanner/console input ---
    public String SentMessage(Scanner scanner) {
        // The full details of each message should be displayed on the screen (using JOptionPane)
        displayMessageDetails(this);

        String[] options = {"Send Message", "Disregard Message", "Store Message (JSON)"};

        int subChoice = JOptionPane.showOptionDialog(
                null,
                "Choose an action for Message No. " + this.numMessagesSent,
                "Send Message Options",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        ); //

        // subChoice: 0=Send, 1=Disregard, 2=Store, -1=Closed Dialog

        switch (subChoice) {
            case 0: // Send Message
                messageList.add(this);
                return "SEND";
            case 1: // Disregard Message
                return "DISREGARD";
            case 2: // Store Message to send later (JSON)
                storeMessage(this);
                messageList.add(this);
                return "STORE";
            default: // Dialogue closed or unexpected result
                return "DISREGARD"; // Treat closing the dialog as discarding the message
        }
    }

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

    public static int returnTotalMessagess() {
        return messageList.size();
    }

    public static void clearMessageListForTesting() {
        messageList.clear();
    }

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

    // --- REVISED: Accepts the correct message count (newCount) ---
    private static void handleMessageCreation(Scanner scanner, int newCount) {

        String recipient = getValidatedRecipient(scanner);

        String messageText = getValidatedMessageText(scanner);

        // Use the count passed from the loop
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

    // Public getters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public int getNumMessagesSent() { return numMessagesSent; }
}