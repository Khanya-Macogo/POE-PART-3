import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login {

    //database for a single user
    private String registeredUsername;
    private String registeredPassword;
    private String registeredName; // You need this for the welcome message

    public void registerUser(String username, String password, String name) {
        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredName = name;
    }

    //check username conditions
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    //check password complexity rules
    public boolean checkPasswordComplexity(String password) {
        //eight characters
        boolean isLongEnough = password.length() >= 8;
        //capital letter
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        //Contains a number
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        //Contains a special character
        boolean hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        return isLongEnough && hasCapital && hasNumber && hasSpecialChar;
    }

    //check cell phone number format
    public boolean checkCellPhoneNumber(String cellNumber) {
        Pattern pattern = Pattern.compile("^\\+27\\d{9,10}$");
        Matcher matcher = pattern.matcher(cellNumber);
        return matcher.matches();
    }

    //register user
    public String registerUser(String username, String password, String cellNumber, String firstName, String lastName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredName = firstName + " " + lastName;

        return "Registration successful!";
    }

    public boolean loginUser(String username, String password) {
        if (registeredUsername != null && registeredUsername.equals(username) && registeredPassword.equals(password)) {
            return true;
        }
        return false;
    }

    //return the appropriate login message
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            String[] names = registeredName.split(" ");
            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect please try again.";
        }
    }
}