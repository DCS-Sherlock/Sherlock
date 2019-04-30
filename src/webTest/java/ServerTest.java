import Utils.AccountUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest extends AbstractWebTest {

    @BeforeAll
    public static void setupClass() {

    }

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        setDirectory("ServerTests");
        //return to home page
        browser.get(baseURL);
        setSubDirectory("");
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    //Test to shows that a user can successfully login and out of the Sherlock system.
    @Test
    public void canLoginAndOut() {
        setSubDirectory("CanLoginAndOut");
        AccountUtils.navigateToLogin(getSettings());
        // Enter Username and Password
        browser.findElement(By.id("username")).sendKeys(AccountUtils.getAdminEmail(getSettings()));
        browser.findElement(By.id("password")).sendKeys(AccountUtils.getAdminPassword(getSettings()));
        takeScreenshot("01_LoginPage.jpg");
        // Click the login button
        browser.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block")).click();
        takeScreenshot("02_LoggedIn.jpg");
        String dashboardTitle = messageProperties.getProperty("dashboard.title");
        assertTrue(browser.getTitle().contains(dashboardTitle), "Page title does not line up with .messageProperties file");
        browser.findElement(By.cssSelector(".nav-link.dropdown-toggle")).click();
        takeScreenshot("03_ClickedMenuBar.jpg");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#logout button")));
        browser.findElement(By.cssSelector("form#logout button")).click();
        takeScreenshot("04_Logout.jpg");
        String logoutMessage = messageProperties.getProperty("error.logged_out");
        assertEquals(browser.findElement(By.cssSelector("div.alert")).getText(), logoutMessage, "Logout message does not line up with .messageProperties file");
    }

    //Test to show that a login attempt using an incorrect email and password will fail.
    @Test
    public void canFailLogin() {
        setSubDirectory("LoginFailure");
        // GoTo Login Page
        String loginLinkText = messageProperties.getProperty("nav.login");
        browser.findElement(By.linkText(loginLinkText)).click();
        // Enter Username and Password
        browser.findElement(By.id("username")).sendKeys("Wrong");
        browser.findElement(By.id("password")).sendKeys("Wrong");
        takeScreenshot("01_BadDetails.jpg");
        // Click the login button
        browser.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block")).click();
        takeScreenshot("02_Failure.jpg");
        assertTrue(browser.getTitle().contains("Login"), "Failing to Login does not loop back to Login Page");
        String loginFailMessage = messageProperties.getProperty("error.invalid_login");
        assertEquals(browser.findElement(By.cssSelector("div.alert")).getText(), loginFailMessage, "Login error message does not line up with .messageProperties file");
    }
}
