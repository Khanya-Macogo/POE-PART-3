import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login auth = new Login();

        //User Registration
        System.out.println("Register a new account");

        System.out.println("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.println("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.println("Create a username (must have an '_' and be <= 5 chars): ");
        String regUsername = scanner.nextLine();

        System.out.println("Create a password (>= 8 chars, incl. capital, number, special char): ");
        String regPassword = scanner.nextLine();

        System.out.println("Enter your SA cell number (e.g., +27821234567): ");
        String regCell = scanner.nextLine();

        String registrationMessage = auth.registerUser(regUsername, regPassword, regCell, firstName, lastName);
        System.out.println(registrationMessage);


        if (registrationMessage.equals("Registration successful!")) {
            // User Login
            System.out.println("\nNow log in to your account ");

            System.out.println("Enter your username: ");
            String loginUsername = scanner.nextLine();

            System.out.println("Enter your password: ");
            String loginPassword = scanner.nextLine();

            String loginMessage = auth.returnLoginStatus(loginUsername, loginPassword);
            System.out.println(loginMessage);
        }

        boolean loggedIn = true;

        Message.runQuickChatApp();

        scanner.close();
    }
}