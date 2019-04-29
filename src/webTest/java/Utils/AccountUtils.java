package Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountUtils {

    // return a hardcoded value for login details
    public static String getDefaultUsername() {
        return "admin.sherlock@example.com";
    }

    // return a hardcoded value for login details
    public static String getDefaultPassword() {
        return "admin_password";
    }

    public static String getAdminEmail(TestSettings settings) {
        return settings.applicationServerProperties.getProperty("sherlock.setup.email");
    }

    public static String getAdminName(TestSettings settings) {
        return settings.applicationServerProperties.getProperty("sherlock.setup.name");
    }

    public static String getAdminPassword(TestSettings settings) {
        return settings.applicationServerProperties.getProperty("sherlock.setup.password");
    }

    public static void loginWithAdmin(TestSettings settings) {
        // GoTo Login Page
        navigateToLogin(settings);
        // Enter Username and Password
        //settings.browser.findElement(By.id("username")).sendKeys(getAdminEmail(settings));
        //settings.browser.findElement(By.id("password")).sendKeys(getAdminPassword(settings));
        settings.browser.findElement(By.id("username")).sendKeys(getDefaultUsername());
        settings.browser.findElement(By.id("password")).sendKeys(getDefaultPassword());

        // Click the login button
        settings.browser.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block")).click();
    }

    public static void loginWithDetails(TestSettings settings, String accountEmail, String accountPassword) {
        navigateToLogin(settings);
        // Enter Username and Password
        settings.browser.findElement(By.id("username")).sendKeys(accountEmail);
        settings.browser.findElement(By.id("password")).sendKeys(accountPassword);
        // Click the login button
        settings.browser.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block")).click();
    }

    public static void logOut(TestSettings settings) {
        NavigateUtils.get(settings, NavEnum.DASHBOARD);
        try {
            settings.browser.findElement(By.cssSelector(".nav-link.dropdown-toggle")).click();
            settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#logout button")));
            settings.browser.findElement(By.cssSelector("form#logout button")).click();
        } catch (Exception e) {
            System.out.println("Could not log out");
        }
    }

    public static String addAccount(TestSettings settings, String newAccountName, String newAccountEmail) {
        navigateToAdminSettings(settings);
        settings.browser.findElement(By.cssSelector(".btn.btn-primary")).click();

        WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(newAccountName);
        modal.findElement(By.cssSelector("#email")).sendKeys(newAccountEmail);
        modal.findElement(By.cssSelector("#oldPassword")).sendKeys(getDefaultPassword());
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();

        Sleeper.sleep();
        modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        Sleeper.sleep();
        WebElement passwordText = settings.wait.until(ExpectedConditions.elementToBeClickable((By.cssSelector(".modal-body"))));
        String newAccountPassword =  passwordText.findElement(By.cssSelector("#newPassword")).getAttribute("value");
        modal.findElement(By.cssSelector(".btn.btn-secondary")).click();

        return newAccountPassword;
    }

    public static void deleteAccount(TestSettings settings, String accountEmail) {
        navigateToAdminSettings(settings);
        WebElement searchbox = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.form-control")));
        searchbox.sendKeys(accountEmail);
        searchbox.sendKeys(Keys.ENTER);
        Sleeper.sleep();
        WebElement table = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".table.table-hover.table-borderless")));
        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            String selectedAccount = row.findElement(By.cssSelector("td.align-middle")).getText();
            if (selectedAccount.equals(accountEmail)) {
                row.findElement(By.cssSelector(".btn.btn-primary.dropdown-toggle")).click();

                row.findElement(By.cssSelector("a.dropdown-item.delete")).click();
                WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
                modal.findElement(By.cssSelector("#confirmPassword")).sendKeys(getDefaultPassword());
                modal.findElement(By.cssSelector(".modal-footer .btn.btn-primary")).click();
                break;
            }
        }
    }

    public static boolean searchForAccount(TestSettings settings, String accountEmail) {
        navigateToAdminSettings(settings);
        settings.browser.findElement(By.cssSelector("input.form-control")).sendKeys(accountEmail);
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.btn-primary")));
        boolean found = false;
        List<WebElement> rows = settings.browser.findElements(By.cssSelector("div#list-parent table tr"));
        for (WebElement row : rows) {
            String selectedWorkspaceName = row.findElement(By.cssSelector("td.align-middle")).getText();
            if (selectedWorkspaceName.equals(accountEmail)) {
                row.findElement(By.cssSelector(".btn.btn-primary")).click();
                found = true;
                break;
            }
        }
        return found;
    }

    public static void navigateToLogin(TestSettings settings) {
        NavigateUtils.get(settings, NavEnum.LOGIN);
    }

    public static void navigateToAdminSettings(TestSettings settings) {
        NavigateUtils.get(settings, NavEnum.ADMIN);
    }
}


