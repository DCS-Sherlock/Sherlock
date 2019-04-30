import Utils.AccountUtils;
import Utils.Sleeper;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


import static org.junit.jupiter.api.Assertions.*;

public class AdminTest extends AbstractWebTest {
    @BeforeAll
    public static void setupClass() {

    }

    @BeforeEach
    public void setupTest() {
        //return to home page
        setDefaultTestSettings();
        AccountUtils.loginWithAdmin(getSettings());
        setDirectory("AdminTests");
        setSubDirectory("");
        browser.get(baseURL);
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    //Test to show that it is possible to add an account to the Sherlock system.
    @Test
    public void addAccount() {
        setSubDirectory("AddAccount");
        AccountUtils.navigateToAdminSettings(getSettings());
        browser.findElement(By.cssSelector(".btn.btn-primary")).click();

        String newAccountName = "User2";
        String newAccountEmail = "user2@sherlock.com";
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(newAccountName);
        modal.findElement(By.cssSelector("#email")).sendKeys(newAccountEmail);
        modal.findElement(By.cssSelector("#oldPassword")).sendKeys(AccountUtils.getAdminPassword(getSettings()));
        takeScreenshot("01_userAccountDetails.jpg");
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();


        modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        takeScreenshot("02_AccountPassword.jpg");
        String alertSuccess = messageProperties.getProperty("admin.accounts.password.start");
        Sleeper.sleep();
        String alertText = wait.until(ExpectedConditions.visibilityOf(modal.findElement(By.cssSelector("div.alert")))).getText();
        assertEquals(alertSuccess, alertText);
        String newAccountPassword = modal.findElement(By.cssSelector("input#newPassword")).getAttribute("value");
        modal.findElement(By.cssSelector(".btn.btn-secondary")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.form-control"))).sendKeys(newAccountEmail);
        takeScreenshot("03_accountInList.jpg");

        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithDetails(getSettings(), newAccountEmail, newAccountPassword);
        takeScreenshot("04_loggedInToAccount.jpg");
        //assert that url = dashboard
        String expected = baseURL + "dashboard/index";
        assertEquals(expected, browser.getCurrentUrl());

        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithAdmin(getSettings());
        AccountUtils.deleteAccount(getSettings(), newAccountEmail);
    }

    //Test to shows that adding an account with an email that has already been registered will fail.
    @Test
    public void duplicateAccountFail() {
        setSubDirectory("DuplicateAccountFail");
        AccountUtils.navigateToAdminSettings(getSettings());

        String originalAccountName = "User";
        String copyAccountName = "Copy";
        String sharedAccountEmail = "user@sherlock.com";
        AccountUtils.addAccount(getSettings(), originalAccountName, sharedAccountEmail);
        takeScreenshot("01_OriginalAccount.jpg");

        AccountUtils.navigateToAdminSettings(getSettings());
        browser.findElement(By.cssSelector(".btn.btn-primary")).click();

        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(copyAccountName);
        modal.findElement(By.cssSelector("#email")).sendKeys(sharedAccountEmail);
        modal.findElement(By.cssSelector("#oldPassword")).sendKeys(AccountUtils.getAdminPassword(getSettings()));
        takeScreenshot("02_duplicateAccountDetails.jpg");
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();

        Sleeper.sleep();
        modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        Sleeper.sleep();
        takeScreenshot("03_duplicateAccountAlertText.jpg");
        String alertText = modal.findElement(By.cssSelector("div.alert")).getText();
        String expectedText = messageProperties.getProperty("error.email.exists");
        assertEquals(expectedText, alertText);

        AccountUtils.deleteAccount(getSettings(), sharedAccountEmail);
    }

}
