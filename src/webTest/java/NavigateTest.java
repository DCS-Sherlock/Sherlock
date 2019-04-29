import Utils.AccountUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigateTest extends AbstractWebTest {

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        setDirectory("NavigateTests");
        //return to home page
        browser.get(baseURL);
        setSubDirectory("");
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    //Test to shows that the navigation bar that is used when the user is logged out works.
    @Test
    public void testLoggedOutNavBar() {
        setSubDirectory("LoggedOutNavBar");
        assertEquals(baseURL, browser.getCurrentUrl(), "The home page URL is unexpected");
        String homeTitle = messageProperties.getProperty("home.title");
        assertTrue(browser.getTitle().contains(homeTitle), "Page title does not line up with .messageProperties file");
        takeScreenshot("01_HomePage.jpg");

        String helpLinkText = getSettings().messageProperties.getProperty("nav.help");
        String helpTitle = getSettings().messageProperties.getProperty("help.title");
        browser.findElement(By.linkText(helpLinkText)).click();
        takeScreenshot("02_HelpPage.jpg");
        String expectedURL = baseURL + "help";
        assertEquals(expectedURL, browser.getCurrentUrl());
        assertTrue(browser.getTitle().contains(helpTitle));


        String loginLinkText = getSettings().messageProperties.getProperty("nav.login");
        String loginTitle = getSettings().messageProperties.getProperty("login.title");
        browser.findElement(By.linkText(loginLinkText)).click();
        takeScreenshot("03_LoginPage.jpg");
        expectedURL = baseURL + "login";
        assertEquals(expectedURL, browser.getCurrentUrl());
        assertTrue(browser.getTitle().contains(loginTitle));
    }

    //Test to shows that the navigation bar that the user is presented when they have logged in works.
    @Test
    public void testLoggedInNavBar() {
        setSubDirectory("LoggedInNavBar");
        AccountUtils.loginWithAdmin(getSettings());

        assertEquals(baseURL + "dashboard/index", browser.getCurrentUrl(), "The home page URL is unexpected");
        String homeTitle = messageProperties.getProperty("dashboard.title");
        assertTrue(browser.getTitle().contains(homeTitle), "Page title does not line up with .messageProperties file");
        takeScreenshot("01_Dashboard.jpg");

        String expectedURL = "";

        String workspacesLinkText = getSettings().messageProperties.getProperty("nav.workspaces");
        String workspacesTitle = getSettings().messageProperties.getProperty("workspaces.title");
        browser.findElement(By.linkText(workspacesLinkText)).click();
        takeScreenshot("02_Workspaces.jpg");
        expectedURL = baseURL + "dashboard/workspaces";
        assertEquals(expectedURL, browser.getCurrentUrl());
        assertTrue(browser.getTitle().contains(workspacesTitle));

        String templatesLinkText = getSettings().messageProperties.getProperty("nav.templates");
        String templatesTitle = getSettings().messageProperties.getProperty("templates.title");
        browser.findElement(By.linkText(templatesLinkText)).click();
        takeScreenshot("03_Templates.jpg");
        expectedURL = baseURL + "dashboard/templates";
        assertEquals(expectedURL, browser.getCurrentUrl());
        assertTrue(browser.getTitle().contains(templatesTitle));

        String helpLinkText = getSettings().messageProperties.getProperty("nav.help");
        String helpTitle = getSettings().messageProperties.getProperty("help.title");
        browser.findElement(By.linkText(helpLinkText)).click();
        takeScreenshot("04_HelpPage.jpg");
        expectedURL = baseURL + "help";
        assertEquals(expectedURL, browser.getCurrentUrl());
        assertTrue(browser.getTitle().contains(helpTitle));
    }
}
