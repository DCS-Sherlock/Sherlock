import Utils.AccountUtils;
import Utils.TemplateUtils;
import Utils.WorkspaceUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PrivateAccessTests extends AbstractWebTest {

    private String username = "NewAccount";
    private String userEmail = "new@sherlock.com";
    private String userPassword = "";

    @BeforeEach
    public void setupTest() {
        setDefaultTestSettings();
        setDirectory("PrivateAccessTests");
        AccountUtils.loginWithAdmin(getSettings());
        browser.get(baseURL);
        setSubDirectory("");
        userPassword = AccountUtils.addAccount(getSettings(), username, userEmail);
        browser.get(baseURL);
    }

    @AfterEach
    public void tearDown() {
        AccountUtils.deleteAccount(getSettings(), userEmail);
        browser.close();
    }

    //Test to ensure that templates created by an account are private to an account.
    @Test
    public void templatesArePrivate() {
        setSubDirectory("TemplatesArePrivate");
        String templateName = "privateTemplate";
        TemplateUtils.addTemplate(getSettings(), templateName);
        takeScreenshot("01_CreatePrivateTemplate.jpg");
        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithDetails(getSettings(), userEmail, userPassword);
        takeScreenshot("02_LoginAsNew.jpg");
        boolean success = TemplateUtils.selectTemplate(getSettings(), templateName);
        assertFalse(success);
        takeScreenshot("03_NoPrivateTemplate.jpg");

        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithAdmin(getSettings());
        TemplateUtils.deleteTemplate(getSettings(), templateName);
    }

    //Test to verify that workspaces created by an account are private.
    @Test
    public void workspacesArePrivate() {
        setSubDirectory("WorkspacesArePrivate");
        String workspaceName = "privateWorkspace";
        WorkspaceUtils.addWorkspace(getSettings(), workspaceName);
        takeScreenshot("01_CreatePrivateWorkspace.jpg");
        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithDetails(getSettings(), userEmail, userPassword);
        takeScreenshot("02_LoginAsNew.jpg");
        boolean success = WorkspaceUtils.selectWorkspace(getSettings(), workspaceName);
        assertFalse(success);
        takeScreenshot("03_NoPrivateWorkspace.jpg");

        AccountUtils.logOut(getSettings());
        AccountUtils.loginWithAdmin(getSettings());
        WorkspaceUtils.deleteWorkspace(getSettings(), workspaceName);
    }
}
