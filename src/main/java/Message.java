import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Message {

    private static final String WELCOME_MESSAGE = "Welcome to QuickChat";
    private static final String COMING_SOON = "Feature is still in development. Coming Soon!";
    private static final int MAX_MSG_TEXT_LENGTH = 250;
    private static final int MAX_RECIPIENT_LENGTH = 12;
    private static final Random RANDOM = new Random();

    public static final List<Message> messageList = new ArrayList<>();

    public static final List<String> disregardedMessages = new ArrayList<>();
    public static final List<String> messageHashes = new ArrayList<>();
    public static final List<String> messageIDs = new ArrayList<>();
    // ---------------------------------------------------------------------

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

        messageIDs.add(this.messageID);
        messageHashes.add(this.messageHash);
    }

    public Message() {
    }

    public static List<Message> getMessageList() {
        return messageList;
    }

    public static void runQuickChatApp() {
        Scanner scanner = new Scanner(System.in);

        JOptionPane.showMessageDialog(null, WELCOME_MESSAGE, "Welcome to QuickChat", JOptionPane.INFORMATION_MESSAGE);

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
            String menuOptions;

            if (currentMessagesSent >= maxMessagesLimit) {
                String limitReachedMsg = String.format(
                        "Message Limit Reached: %d/%d messages sent.\n",
                        currentMessagesSent, maxMessagesLimit);
                JOptionPane.showMessageDialog(null, limitReachedMsg, "Limit Reached", JOptionPane.WARNING_MESSAGE);
                menuOptions = """
                        1) Send Messages (Disabled)
                        2) Show recently sent/stored messages
                        3) Quit
                        4) Data Analysis and Reporting""";
            } else {
                menuOptions = String.format("1) Send Messages (Sent: %d/%d)\n", currentMessagesSent, maxMessagesLimit) +
                        "2) Show recently sent/stored messages\n" +
                        "3) Quit\n" +
                        "4) Data Analysis and Reporting";
            }

            String input = JOptionPane.showInputDialog(
                    null,
                    menuOptions + "\n\nEnter your choice (1-4):",
                    "QuickChat Menu",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input == null) {
                choice = 3;
            } else {
                try {
                    choice = Integer.parseInt(input.trim());

                    switch (choice) {
                        case 1:
                            if (currentMessagesSent < maxMessagesLimit) {
                                int remainingSlots = maxMessagesLimit - currentMessagesSent;
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

                                        for (int i = 0; i < numToSend; i++) {
                                            int uniqueNewCount = currentMessagesSent + (i + 1);
                                            System.out.println("\n--- Entering Message " + (i + 1) + " of " + numToSend + " (Total Message No.: " + uniqueNewCount + ") ---");
                                            // Pass the main scanner
                                            handleMessageCreation(scanner, uniqueNewCount);

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
                                JOptionPane.showMessageDialog(null, "Message limit reached. Please choose option 2, 3, or 4.", "Limit Reached", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case 2:
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
                        case 4:
                            showDataAnalysisMenu();
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3, or 4.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number (1-4).", "Input Error", JOptionPane.ERROR_MESSAGE);
                    choice = 0;
                }
            }
        }

        scanner.close();
    }

    public static void showDataAnalysisMenu() {
        int subChoice = 0;

        while (subChoice != 8) {
            String menu = """
                --- Data Analysis Menu (Requirement 2) ---
                1) Display sender and recipient of all sent messages.
                2) Display the longest sent message.
                3) Search for a message ID and display details.
                4) Search for all messages sent to a recipient.
                5) Delete a message using the message hash.
                6) Display a full details report of all sent messages.
                7) Load Stored Message into history.
                8) Back to main menu
                """;

            String input = JOptionPane.showInputDialog(null, menu + "\nEnter your choice (1-8):", "Data Analysis", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                subChoice = 8;
                break;
            }

            try {
                subChoice = Integer.parseInt(input.trim());
                String result = "";
                String searchKey;

                switch (subChoice) {
                    case 1:
                        result = displaySenderAndRecipient();
                        JOptionPane.showMessageDialog(null, result, "Sender & Recipient List", JOptionPane.PLAIN_MESSAGE);
                        break;
                    case 2:
                        result = displayLongestSentMessage();
                        JOptionPane.showMessageDialog(null, result, "Longest Message", JOptionPane.PLAIN_MESSAGE);
                        break;
                    case 3:
                        searchKey = JOptionPane.showInputDialog(null, "Enter the Message ID to search:");
                        if (searchKey != null) {
                            result = searchMessageByID(searchKey.trim());
                            JOptionPane.showMessageDialog(null, result, "Search by ID Result", JOptionPane.PLAIN_MESSAGE);
                        }
                        break;
                    case 4:
                        searchKey = JOptionPane.showInputDialog(null, "Enter the Recipient Cell Number to search (e.g., +27123456789):");
                        if (searchKey != null) {
                            result = searchMessagesByRecipient(searchKey.trim());
                            JOptionPane.showMessageDialog(null, result, "Search by Recipient Result", JOptionPane.PLAIN_MESSAGE);
                        }
                        break;
                    case 5:
                        searchKey = JOptionPane.showInputDialog(null, "Enter the Message Hash to delete:");
                        if (searchKey != null) {
                            result = deleteMessageByHash(searchKey.trim());
                            JOptionPane.showMessageDialog(null, result, "Delete by Hash Result", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case 6:
                        result = displayFullDetailsReport();
                        JOptionPane.showMessageDialog(null, result, "Full Sent Messages Report", JOptionPane.PLAIN_MESSAGE);
                        break;
                    case 7:
                        searchKey = JOptionPane.showInputDialog(null,
                                "Enter the Message ID of the file you want to load (e.g., 1012345678):");
                        if (searchKey != null) {
                            result = readStoredMessages(searchKey.trim());
                            JOptionPane.showMessageDialog(null, result, "Load Stored Message Result", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case 8:
                        JOptionPane.showMessageDialog(null, "Returning to main menu.", "Navigation", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1-8.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static String displaySenderAndRecipient() {
        if (messageList.isEmpty()) return "No sent messages found.";

        StringBuilder sb = new StringBuilder("--- Sent Messages: Sender & Recipient ---\n");
        String sender = "QuickChat User";

        for (Message msg : messageList) {
            sb.append(String.format("Sender: %s | Recipient: %s\n", sender, msg.recipient));
        }
        return sb.toString();
    }

    public static String displayLongestSentMessage() {
        if (messageList.isEmpty()) return "No sent messages to compare.";

        String longestMessage = "";
        int maxLength = 0;

        for (Message msg : messageList) {
            String currentText = msg.messageText;
            if (currentText != null && currentText.length() > maxLength) {
                maxLength = currentText.length();
                longestMessage = currentText;
            }
        }

        if (longestMessage.isEmpty() && messageList.size() > 0) return "All messages were empty or null.";

        return String.format("--- Longest Sent Message (Length: %d) ---\n\"%s\"", maxLength, longestMessage);
    }

    // --- R2.c: Search for a message ID and display corresponding details ---
    public static String searchMessageByID(String searchID) {
        for (Message msg : messageList) {
            if (msg.messageID.equals(searchID)) {
                return String.format(
                        """
                        --- Message Found ---
                        Message ID: %s
                        Recipient: %s
                        Message: %s
                        """,
                        msg.messageID,
                        msg.recipient,
                        msg.messageText
                );
            }
        }
        return "Message ID '" + searchID + "' not found in sent/stored messages.";
    }

    public static String searchMessagesByRecipient(String searchRecipient) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (Message msg : messageList) {
            if (msg.recipient.equals(searchRecipient)) {
                sb.append(String.format(" - ID: %s | Message: \"%s\"\n", msg.messageID, msg.messageText));
                found = true;
            }
        }

        if (!found) return "No messages found for recipient: " + searchRecipient;

        sb.insert(0, "--- Messages Found for Recipient: " + searchRecipient + " ---\n");
        return sb.toString();
    }

    public static String deleteMessageByHash(String searchHash) {
        for (int i = 0; i < messageList.size(); i++) {
            Message msg = messageList.get(i);
            if (msg.messageHash.equals(searchHash)) {
                messageList.remove(i);
                return "Message with Hash '" + searchHash + "' deleted successfully.";
            }
        }
        return "Message Hash '" + searchHash + "' not found in sent/stored messages.";
    }

    public static String displayFullDetailsReport() {
        if (messageList.isEmpty()) {
            return "\n--- Full Sent Message Report ---\nNo messages sent or stored yet.\n----------------------------------\n";
        }

        StringBuilder sb = new StringBuilder("\n--- Full Sent Message Report ---\n");
        for (Message msg : messageList) {
            sb.append(String.format("Message No: %d\n", msg.numMessagesSent));
            sb.append(String.format("ID: %s | Recipient: %s\n", msg.messageID, msg.recipient));
            sb.append(String.format("Hash: %s\n", msg.messageHash));
            sb.append(String.format("Message Text: \"%s\"\n", msg.messageText));
            sb.append("-------------------------------------------\n");
        }
        return sb.toString();
    }

    private static String loadMessageFromFile(String fileName) {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line).append("\n");
            }
            return jsonContent.toString();
        } catch (FileNotFoundException e) {
            return "ERROR: File not found. Ensure " + fileName + " exists.";
        } catch (IOException e) {
            return "ERROR: An error occurred while reading the file: " + e.getMessage();
        }
    }

    public static String readStoredMessages(String searchID) {
        String fileName = "message_" + searchID + ".json";
        String jsonString = loadMessageFromFile(fileName);

        if (jsonString.startsWith("ERROR")) {
            return jsonString;
        }

        try {
            String numSentStr = jsonString.substring(jsonString.indexOf("\"NumSent\":") + 11);
            numSentStr = numSentStr.substring(0, numSentStr.indexOf(",")).trim();
            int numSent = Integer.parseInt(numSentStr);

            // Extract Recipient
            String recipient = jsonString.substring(jsonString.indexOf("\"Recipient\":") + 14);
            recipient = recipient.substring(0, recipient.indexOf("\""));

            // Extract Message Text
            String messageText = jsonString.substring(jsonString.indexOf("\"Message\":") + 12);
            messageText = messageText.substring(0, messageText.indexOf("\""));

            // Create a new Message object. ID/Hash are regenerated.
            Message loadedMessage = new Message(messageText, recipient, numSent);

            // Check if this message (by hash, which is based on content/count) is already in the list
            boolean isDuplicate = messageList.stream()
                    .anyMatch(m -> m.getMessageHash().equals(loadedMessage.getMessageHash()));

            if (!isDuplicate) {
                messageList.add(loadedMessage);
                return "Successfully loaded and added stored message (ID: " + loadedMessage.getMessageID() + ") to history.";
            } else {
                return "Message content was already present in history. Load aborted.";
            }

        } catch (Exception e) {
            return "ERROR: Failed to parse JSON content from " + fileName + ". Check file integrity.";
        }
    }

    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }

    public int checkRecipientCell() {
        if (this.recipient == null) return 0;
        if (this.recipient.length() > MAX_RECIPIENT_LENGTH) return 0;
        if (!this.recipient.startsWith("+")) return 0;
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

    public String SentMessage(Scanner scanner) {
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
        );

        switch (subChoice) {
            case 0: // Send Message
                messageList.add(this);
                return "SEND";
            case 1: // Disregard Message
                disregardedMessages.add(this.messageText); // <--- Populates Requirement 1 list
                return "DISREGARD";
            case 2: // Store Message to send later (JSON)
                storeMessage(this);
                messageList.add(this); // Add to messageList so it appears in history/reports
                return "STORE";
            default: // Dialogue closed or unexpected result
                disregardedMessages.add(this.messageText);
                return "DISREGARD";
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
                msg.messageID, msg.numMessagesSent, msg.recipient, msg.messageText.replace("\"", "\\\""), msg.messageHash
        ); // Used .replace for basic JSON safety against embedded quotes

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonContent);
            System.out.println("File saved successfully to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the JSON file: " + e.getMessage());
        }
    }

    private static void handleMessageCreation(Scanner scanner, int newCount) {
        String recipient = getValidatedRecipient(scanner);
        String messageText = getValidatedMessageText(scanner);
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
                System.out.println("Please enter a message of less than " + MAX_MSG_TEXT_LENGTH + " characters.");
            } else {
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