import Utils.NavEnum;
import Utils.NavigateUtils;
import Utils.Sleeper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpCentreTest extends AbstractWebTest {

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        setDirectory("HelpCentreTests");
        //return to home page
        browser.get(baseURL);
        setSubDirectory("");
        NavigateUtils.get(getSettings(), NavEnum.HELP);
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }


    //Test to show that the help centre page is populated with the text from the help.properties resource file.
    @Test
    public void helpQustions() {
        setSubDirectory("HelpCentre");
        Properties helpProperties = loadPropertiesFromFile(defaultHelpPropertiesPath);
        List<WebElement> cards = browser.findElements(By.cssSelector(".accordion .card"));
        takeScreenshot("01_Questions.jpg");
        WebElement card = cards.get(0);
        card.click();
        String cardQuestionText = card.findElement(By.cssSelector(".btn-link span")).getText();
        String firstProperty = "analysis_frozen";
        String expectedQuestionText = helpProperties.getProperty(firstProperty);

        assertEquals(expectedQuestionText, cardQuestionText);
        Sleeper.sleep();
        String cardAnswerText = card.findElement(By.cssSelector(".answer")).getText();
        String expectedAnswerText = helpProperties.getProperty(firstProperty + "_answer").replaceAll("\\<[^>]*>", "");
        assertEquals(expectedAnswerText, cardAnswerText);
        takeScreenshot("02_Answers.jpg");

    }

    //The tests confirm that the Help Centre can be navigated. Page 1 is the Terms and Conditions, and Page 2 is the Privacy Policy.
    @Test
    public void navigateHelpCentre() {
        setSubDirectory("Navigate");
        String expectedTitle;
        browser.findElement(By.cssSelector(".terms")).click();
        expectedTitle = messageProperties.getProperty("terms.title");
        takeScreenshot("01_TermsPage.jpg");
        assertTrue(browser.getTitle().contains(expectedTitle));

        NavigateUtils.get(getSettings(), NavEnum.HELP);
        browser.findElement(By.cssSelector(".privacy")).click();
        expectedTitle = messageProperties.getProperty("privacy.title");
        takeScreenshot("02_PrivacyPage.jpg");
        assertTrue(browser.getTitle().contains(expectedTitle));
    }


}
