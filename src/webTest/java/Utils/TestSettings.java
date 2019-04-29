package Utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Properties;

public class TestSettings {
    public WebDriver browser;
    public String baseURL;
    public WebDriverWait wait;
    public Properties messageProperties;
    public Properties applicationServerProperties;
}
