import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LoginTest {
    @Test
    public void testValidPhoneNumber() {
        Login login = new Login();
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testInvalidPhoneNumber() {
        Login login = new Login();
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testCorrectUserName() {
        Login login = new Login();
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testIncorrectUserName() {
        Login login = new Login();
        assertFalse(login.checkUserName("kyle!!!!!!!!!!"));
    }

    @Test
    public void testValidPassword() {
        Login login = new Login();
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testInvalidPassword() {
        Login login = new Login();
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testLoginSuccess() {
        Login login = new Login();
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kyle Smith");
        String status = login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        boolean loginStatus = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        Assert.assertTrue(loginStatus);
        Assert.assertEquals("Welcome Kyle, Smith it is great to see you again.", status);
    }

    @Test
    public void testLoginUnsuccessful() {
        Login login = new Login();
        login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        assertFalse(login.loginUser("ky1", "WrongPass"));
    }
}
