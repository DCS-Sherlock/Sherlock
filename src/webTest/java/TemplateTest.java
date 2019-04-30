import Utils.AccountUtils;
import Utils.Sleeper;
import Utils.TemplateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateTest extends AbstractWebTest {

    @BeforeAll
    public static void setupClass() {
    }

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        browser.get(baseURL);
        AccountUtils.loginWithAdmin(getSettings());
        setDirectory("TemplateTests");
        setSubDirectory("");

    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    //Test to show that it is possible to add a template to the Sherlock system.
    @Test
    public void addNewTemplate() {
        setSubDirectory("AddTemplate");
        TemplateUtils.navigateToTemplates(getSettings());

        String templateName = "templateToAdd";
        browser.findElement(By.linkText("Add New")).click();

        // first form page
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(templateName);

        WebElement dropdown = modal.findElement(By.cssSelector("#language"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Java");

        List<WebElement> checkboxes = modal.findElements(By.cssSelector("label.checkbox-inline"));
        checkboxes.get(0).findElement(By.cssSelector("input")).click();
        takeScreenshot("01_Settings.jpg");

        modal.findElement(By.cssSelector(".btn.btn-primary")).click();
        String homeLink = messageProperties.getProperty("nav.home");
        String templatesLinkText = messageProperties.getProperty("nav.templates");
        wait.until(ExpectedConditions.and(ExpectedConditions.elementToBeClickable(By.linkText(homeLink)), ExpectedConditions.elementToBeClickable(By.linkText(templatesLinkText))));
        String templateTitle = messageProperties.getProperty("templates.manage.title");
        takeScreenshot("02_Template.jpg");
        String errorMessage = "expected title: " + browser.getTitle() + " to contain: " + templateTitle;
        assertTrue(browser.getTitle().contains(templateTitle), errorMessage);
        TemplateUtils.navigateToTemplates(getSettings());
        TemplateUtils.deleteTemplate(getSettings(), templateName);
    }

    //This test is to show that a template that has been set to public is accessible to other accounts.
    @Test
    public void setPublicTemplate() {
        setSubDirectory("SetPublicTemplate");
        String publicTemplate = "Public";
        TemplateUtils.addTemplate(getSettings(), publicTemplate);
        TemplateUtils.selectTemplate(getSettings(), publicTemplate);

        Sleeper.sleep();
        browser.findElement(By.cssSelector("input#isPublic")).click();
        browser.findElement(By.cssSelector("#details-parent button#update")).click();
        takeScreenshot("01_PublicTemplateOwner.jpg");

        String accountName = "test";
        String accountEmail = "email@test.com";
        String password = AccountUtils.addAccount(getSettings(), accountName, accountEmail);
        takeScreenshot("02_NewAccount.jpg");
        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithDetails(getSettings(), accountEmail, password);

        TemplateUtils.navigateToTemplates(getSettings());
        takeScreenshot("03_ListOfAccessibleTemplates.jpg");
        boolean found = TemplateUtils.selectTemplate(getSettings(), publicTemplate);
        takeScreenshot("04_PublicTemplateGuest.jpg");
        assertTrue(found);

        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithAdmin(getSettings());
        AccountUtils.deleteAccount(getSettings(), accountEmail);
        TemplateUtils.deleteTemplate(getSettings(), publicTemplate);
    }

    //Test to show that a template can be managed and changed.
    @Test
    public void manageTemplate() {

        setSubDirectory("ManageTemplate");
        String templateToManage = "templateToManage";
        TemplateUtils.navigateToTemplates(getSettings());
        TemplateUtils.addTemplate(getSettings(), templateToManage);
        TemplateUtils.navigateToTemplates(getSettings());
        takeScreenshot("01_templates.jpg");
        TemplateUtils.selectTemplate(getSettings(), templateToManage);
        takeScreenshot("02_manage.jpg");
        //Update name
        templateToManage = "renamedTemplate";
        WebElement nameDetailsTextBox = browser.findElement(By.cssSelector("input#name"));
        nameDetailsTextBox.clear();
        nameDetailsTextBox.sendKeys(templateToManage);
        browser.findElement(By.cssSelector("#details-parent button#update")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#name")));

        takeScreenshot("03_newName.jpg");
        assertEquals(templateToManage, browser.findElement(By.cssSelector("input#name")).getAttribute("value"));
        String updateAlert = messageProperties.getProperty("templates.details.updated");
        assertEquals(updateAlert, browser.findElement(By.cssSelector("#details-parent div.alert")).getText());

        //Manage active detectors
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#detectors-parent .btn-primary"))).click();
        WebElement inputForm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-body input.form-control")));
        takeScreenshot("04_manageParameters.jpg");
        inputForm.clear();
        inputForm.sendKeys("1.0");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-footer .btn-primary"))).click();
        Sleeper.sleep();
        String actualAlertText = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-body .alert-info span"))).getText();
        String expectedAlertText = messageProperties.getProperty("templates.parameters.updated");
        takeScreenshot("05_updatedParameters.jpg");
        assertEquals(expectedAlertText, actualAlertText);

        //Delete template when done
        TemplateUtils.navigateToTemplates(getSettings());
        TemplateUtils.deleteTemplate(getSettings(), templateToManage);
    }

    //Test to show that it is possible to delete a template from the system.
    @Test
    public void deleteTemplate() {
        TemplateUtils.navigateToTemplates(getSettings());
        setSubDirectory("DeleteTemplate");
        String templateName = "templateToDelete";
        TemplateUtils.addTemplate(getSettings(), templateName);
        TemplateUtils.navigateToTemplates(getSettings());
        takeScreenshot("01_workspaces.jpg");
        if (!TemplateUtils.selectTemplate(getSettings(), templateName)) {
            throw new Error("failed to select template");
        }
        takeScreenshot("02_manage.jpg");

        String deleteLinkText = messageProperties.getProperty("link.delete");
        browser.findElement(By.linkText(deleteLinkText)).click();
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        String alertText = messageProperties.getProperty("templates.delete.warning");
        assertEquals(alertText, modal.findElement(By.cssSelector("div.alert")).getText());
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();
        takeScreenshot("03_removed.jpg");
        assertFalse(TemplateUtils.selectTemplate(getSettings(), templateName));
    }


}
