import Utils.TestSettings;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/*
Class that every E2E web test should inheret from
Contains many common functionality regarding setting up and executing tests
 */
public abstract class AbstractWebTest {

    public static String baseURL = "http://localhost:2218/";
    public static String defaultMessagePropertiesPath = System.getProperty("user.dir") + "\\src\\main\\resources\\messages.properties";
    public static String defaultHelpPropertiesPath = System.getProperty("user.dir") + "\\src\\main\\resources\\help.properties";
    public static String defaultApplicationServerPropertiesPath = System.getProperty("user.dir") + "\\src\\main\\resources\\application-server.properties";
    public static WebDriver browser;
    public static Properties messageProperties;
    public static Properties applicationServerProperties;
    public static WebDriverWait wait;
    private static String directory = "";
    private static String subDirectory = "";

    public static void setDirectory(String dir) {
        directory = dir;
    }

    public static void setSubDirectory(String dir) {
        subDirectory = dir;
    }

    public static ChromeDriver getChromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }

    public static Properties loadPropertiesFromFile(String propertiesFilePath) {
        FileSystemResource resource;
        Properties properties = null;
        try {
            resource = new FileSystemResource(new File(propertiesFilePath));
            resource.getFile().getAbsolutePath();
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void takeScreenshot(String filename) {
        TakesScreenshot driver = (TakesScreenshot) browser;
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        String topDirectory = System.getProperty("user.dir") + "\\screenshots";
        String fullFilePath = topDirectory + File.separator + directory + File.separator + subDirectory;
        File outputFile = new File(fullFilePath, filename);
        try {
            FileUtils.copyFile(screenshot, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scrollToElement(String cssSelector) {
        WebElement element = browser.findElement(By.cssSelector(cssSelector));
        ((JavascriptExecutor) browser).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static void setDefaultTestSettings() {
        browser = getChromeDriver();
        wait = new WebDriverWait(browser, 30);
        messageProperties = loadPropertiesFromFile(defaultMessagePropertiesPath);
        applicationServerProperties = loadPropertiesFromFile(defaultApplicationServerPropertiesPath);
    }

    public static TestSettings getSettings() {
        TestSettings settings = new TestSettings();
        settings.browser = browser;
        settings.baseURL = baseURL;
        settings.wait = wait;
        settings.messageProperties = messageProperties;
        settings.applicationServerProperties = applicationServerProperties;
        return settings;
    }
}
