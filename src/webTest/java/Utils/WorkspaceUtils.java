package Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

public class WorkspaceUtils {
    public static void addWorkspace(TestSettings settings, String workspaceName) {
        navigateToWorkspaces(settings);

        //settings.browser.findElement(By.linkText("Add New")).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add New"))).click();
        // first form page
        WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(workspaceName);

        WebElement dropdown = modal.findElement(By.cssSelector("#language"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Java");
        modal.findElement(By.cssSelector(".btn.btn-primary")).click();
    }

    public static boolean selectWorkspace(TestSettings settings, String workspaceName) {
        navigateToWorkspaces(settings);
        boolean found = false;
        Sleeper.sleep();
        WebElement table = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".table.table-hover.table-borderless")));
        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            String selectedWorkspaceName = row.findElement(By.cssSelector("h5")).getText();
            if (selectedWorkspaceName.equals(workspaceName)) {
                row.findElement(By.cssSelector(".btn.btn-primary.btn-sm")).click();
                found = true;
                break;
            }
        }
        return found;
    }

    public static void deleteWorkspace(TestSettings settings, String workspaceName) {
        navigateToWorkspaces(settings);
        WebElement searchbox = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.form-control")));
        searchbox.sendKeys(workspaceName);
        searchbox.sendKeys(Keys.ENTER);
        WebElement table = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".table.table-hover.table-borderless")));
        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            String selectedWorkspaceName = row.findElement(By.cssSelector("h5")).getText();
            if (selectedWorkspaceName.equals(workspaceName)) {
                row.findElement(By.cssSelector(".btn.btn-primary.dropdown-toggle")).click();
                row.findElement(By.cssSelector("a.dropdown-item")).click();
                WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
                modal.findElement(By.cssSelector(".modal-footer .btn.btn-primary")).click();
                break;
            }
        }
    }

    public static void uploadFileToWorkspace(TestSettings settings, String absoluteFilePath) {
        WebElement uploadButton = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#submissions-parent .btn.btn-primary")));
        uploadButton.click();
        WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_sub_one"))).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_file_one"))).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#num_file_one_div .form-row input"))).sendKeys(absoluteFilePath);
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#num_file_one_div .form-group .btn-primary"))).click();
    }

    public static void uploadZipToWorkspace(TestSettings settings, String absoluteFilePath) {
        WebElement uploadButton = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#submissions-parent .upload-new-submission")));
        uploadButton.click();
        WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_sub_one"))).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#num_file_multi"))).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#archive_yes"))).click();
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#archive_yes_div .form-row input"))).sendKeys(absoluteFilePath);
        settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#archive_yes_div .form-group .btn-primary"))).click();
        Sleeper.sleep();
        modal.findElement(By.cssSelector(".btn-secondary")).click();
    }

    public static void navigateToWorkspaces(TestSettings settings) {
        NavigateUtils.get(settings, NavEnum.WORKSPACES);
    }
}
