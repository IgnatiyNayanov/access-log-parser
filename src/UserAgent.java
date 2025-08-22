import java.util.Locale;

public class UserAgent {
    private final String browser;
    private final String os;
    //Курсовой проект. Задание #1 по теме "Stream API"
    private final boolean isBot;

    public UserAgent(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty() || userAgentString.equals("-")) {
            this.browser = "Unknown";
            this.os = "Unknown";
            this.isBot = false;
            return;
        }
        this.os = parseOS(userAgentString);
        this.browser = parseBrowser(userAgentString);
        //Курсовой проект. Задание #1 по теме "Stream API"
        this.isBot = userAgentString.toLowerCase().contains("bot");
    }

    private String parseOS(String userAgent) {
        userAgent = userAgent.toLowerCase(Locale.ROOT);
        if (userAgent.contains("windows")) {
            return "Windows";
        }
        if (userAgent.contains("mac")) {
            return "macOS";
        }
        if (userAgent.contains("linux")) {
            return "Linux";
        }
        {
            return "Unknown";
        }
    }

    private String parseBrowser(String userAgent) {
        userAgent = userAgent.toLowerCase(Locale.ROOT);
        if (userAgent.contains("edg/") || userAgent.contains("edge/")) {
            return "Edge";
        }
        if (userAgent.contains("opr/") || userAgent.contains("opera/")) {
            return "Opera";
        }
        if (userAgent.contains("firefox/")) {
            return "Firefox";
        }
        if (userAgent.contains("chrome/")) {
            return "Chrome";
        }
        if (userAgent.contains("safari/") && !userAgent.contains("chrome")) {
            return "Safari";
        }
        {
            return "Unknown";
        }
    }

    //Курсовой проект. Задание #1 по теме "Stream API"


    public boolean isBot() {
        return isBot;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOs() {
        return os;
    }
}