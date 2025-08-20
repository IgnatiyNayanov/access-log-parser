import java.util.Locale;

public class UserAgent {
    private final String browser;
    private final String os;

    public UserAgent(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty() || userAgentString.equals("-")) {
            this.browser = "Unknown";
            this.os = "Unknown";
            return;
        }
        this.os = parseOS(userAgentString);
        this.browser = parseBrowser(userAgentString);
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

    public String getBrowser() {
        return browser;
    }

    public String getOs() {
        return os;
    }
}