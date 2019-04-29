package Utils;

public class NavigateUtils {

    public static void get(TestSettings settings, NavEnum navEnum) {
        if (navEnum == NavEnum.HOME) {
            settings.browser.get(settings.baseURL);
        } else if (navEnum == NavEnum.HELP) {
            settings.browser.get(settings.baseURL + "help");
        } else if (navEnum == NavEnum.LOGIN) {
            settings.browser.get(settings.baseURL + "login");
        } else if (navEnum == NavEnum.DASHBOARD) {
            settings.browser.get(settings.baseURL + "dashboard/index");
        } else if (navEnum == NavEnum.WORKSPACES) {
            settings.browser.get(settings.baseURL + "dashboard/workspaces");
        } else if (navEnum == NavEnum.TEMPLATES) {
            settings.browser.get(settings.baseURL + "dashboard/templates");
        } else if (navEnum == NavEnum.ADMIN) {
            settings.browser.get(settings.baseURL + "admin");
        } else if (navEnum == NavEnum.ACCOUNT) {
            settings.browser.get(settings.baseURL + "account");
        }

    }
}

