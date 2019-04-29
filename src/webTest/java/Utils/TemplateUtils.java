package Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class TemplateUtils {
    public static void addTemplate(TestSettings settings, String templateName) {
        navigateToTemplates(settings);

        settings.browser.findElement(By.linkText("Add New")).click();

        // first form page
        WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
        modal.findElement(By.cssSelector("#name")).sendKeys(templateName);

        WebElement dropdown = modal.findElement(By.cssSelector("#language"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Java");

        List<WebElement> checkboxes = modal.findElements(By.cssSelector("label.checkbox-inline"));
        checkboxes.get(0).findElement(By.cssSelector("input")).click();


        modal.findElement(By.cssSelector(".btn.btn-primary")).click();
    }

    public static boolean selectTemplate(TestSettings settings, String templateName) {
        navigateToTemplates(settings);
        boolean found = false;
        WebElement table = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".table.table-hover.table-borderless")));
        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            String selectedWorkspaceName = row.findElement(By.cssSelector("h5 span")).getText();
            if (selectedWorkspaceName.equals(templateName)) {
                row.findElement(By.cssSelector(".btn.btn-primary.btn-sm")).click();
                found = true;
                break;
            }
        }
        return found;
    }

    public static void deleteTemplate(TestSettings settings, String templateToDelete) {
        navigateToTemplates(settings);
        WebElement searchbox = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.form-control")));
        searchbox.sendKeys(templateToDelete);
        searchbox.sendKeys(Keys.ENTER);
        WebElement table = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".table.table-hover.table-borderless")));
        for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
            String selectedWorkspaceName = row.findElement(By.cssSelector("h5 span")).getText();
            if (selectedWorkspaceName.equals(templateToDelete)) {
                row.findElement(By.cssSelector(".btn.btn-primary.dropdown-toggle")).click();
                row.findElement(By.cssSelector("a.dropdown-item")).click();
                WebElement modal = settings.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#modal")));
                modal.findElement(By.cssSelector(".modal-footer .btn.btn-primary")).click();
                break;
            }
        }
    }

    public static void navigateToTemplates(TestSettings settings) {
        NavigateUtils.get(settings, NavEnum.TEMPLATES);
    }
}
