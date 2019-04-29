import Utils.AccountUtils;
import Utils.Sleeper;
import Utils.WorkspaceUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceTest extends AbstractWebTest {
    @BeforeAll
    public static void setupClass() {

    }

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        browser.get(baseURL);
        AccountUtils.loginWithAdmin(getSettings());
        setDirectory("WorkspaceTests");
        setSubDirectory("");
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    //Test to show that it is possible to add a new workspace to the Sherlock system.
    @Test
    public void addNewWorkspace() {
        setSubDirectory("AddNewWorkspace");
        //go to the workspaces page and click add new button
        String workspaceName = "Sherlock";
        WorkspaceUtils.navigateToWorkspaces(getSettings());

        browser.findElement(By.linkText("Add New")).click();
        // first form page
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(workspaceName);
        WebElement dropdown = modal.findElement(By.cssSelector("#language"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Java");
        takeScreenshot("01_settings.jpg");
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();

        //assert that the browser has changed to manage workspace page
        wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(By.cssSelector(".nav-bar-toggler")), ExpectedConditions.elementToBeClickable(By.linkText("Home"))));
        takeScreenshot("02_newWorkspace.jpg");
        String manageWorkspaceTitle = messageProperties.getProperty("workspaces.manage.title");
        assertTrue(browser.getTitle().contains(manageWorkspaceTitle));
        assertEquals(workspaceName, browser.findElement(By.cssSelector("input#name")).getAttribute("value"));
        WorkspaceUtils.navigateToWorkspaces(getSettings());
        WorkspaceUtils.deleteWorkspace(getSettings(), workspaceName);
    }

    //Test to show that a workspace can be managed using the Sherlock system's web interface.
    //Example features of the manage page are the functions to rename the workspace,
    @Test
    public void manageWorkspace() {
        setSubDirectory("ManageWorkspace");
        String workspaceName = "workspaceToManage";
        WorkspaceUtils.addWorkspace(getSettings(), workspaceName);
        WorkspaceUtils.navigateToWorkspaces(getSettings());
        takeScreenshot("01_Workspaces.jpg");
        WorkspaceUtils.selectWorkspace(getSettings(), workspaceName);
        takeScreenshot("02_ManageWorkspace.jpg");
        //Update name
        String newWorkspaceName = "renamedWorkspace";
        WebElement nameDetailsTextBox = browser.findElement(By.cssSelector("input#name"));
        nameDetailsTextBox.clear();
        nameDetailsTextBox.sendKeys(newWorkspaceName);
        browser.findElement(By.cssSelector("#details-parent button.btn.btn-primary.float-right")).click();
        WebElement alertElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#name")));

        takeScreenshot("03_newName.jpg");
        assertEquals(newWorkspaceName, alertElement.getAttribute("value"));
        String updateAlert = messageProperties.getProperty("workspaces.details.updated");
        assertEquals(updateAlert, browser.findElement(By.cssSelector("#details-parent div.alert")).getText());

        //Upload a file
        String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\uk\\ac\\warwick\\dcs\\sherlock\\engine\\EventBus.java";
        WebElement uploadButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#submissions-parent .text-right .btn.btn-primary")));
        uploadButton.click();
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_sub_one"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_file_one"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#num_file_one_div .form-row input"))).sendKeys(filePath);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#num_file_one_div .form-group .btn"))).click();
        alertElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#upload-parent .modal-body div.alert")));
        takeScreenshot("04_uploadFile.jpg");
        String submissionAlert = messageProperties.getProperty("workspaces.submissions.uploaded.no_dups");
        assertEquals(submissionAlert, alertElement.getText());
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-footer .btn-secondary"))).click();

        Sleeper.sleep();
        //Possibly view a file
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#DataTables_Table_1 .view-file"))).click();
        String actualHeaderText = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".card-header h4 span"))).getText();
        String expectedHeaderText = messageProperties.getProperty("submissions.view.title");
        browser.findElement(By.cssSelector(".card-body .btn-link")).click();
        Sleeper.sleep();
        takeScreenshot("05_viewSubmission.jpg");
        assertEquals(expectedHeaderText, actualHeaderText);

        WorkspaceUtils.deleteWorkspace(getSettings(), workspaceName);
    }

    //Test to show that it is possible to delete a workspace from the Sherlock system.
    @Test
    public void deleteWorkspace() {
        WorkspaceUtils.navigateToWorkspaces(getSettings());
        setSubDirectory("DeleteWorkspace");
        String workspaceName = "workspaceToDelete";
        WorkspaceUtils.addWorkspace(getSettings(), workspaceName);
        WorkspaceUtils.navigateToWorkspaces(getSettings());
        takeScreenshot("01_workspaces.jpg");
        WorkspaceUtils.selectWorkspace(getSettings(), workspaceName);
        takeScreenshot("02_manage.jpg");

        String deleteLinkText = messageProperties.getProperty("link.delete");
        browser.findElement(By.linkText(deleteLinkText)).click();
        WebElement modal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        takeScreenshot("03_modal.jpg");
        String alertText = messageProperties.getProperty("workspaces.delete.warning");
        assertEquals(alertText, modal.findElement(By.cssSelector("div.alert")).getText());
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();
        assertFalse(WorkspaceUtils.selectWorkspace(getSettings(), workspaceName));
        takeScreenshot("04_listOfWorkspaces.jpg");
    }


}