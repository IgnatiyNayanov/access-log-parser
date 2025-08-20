import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public class LogEntry {

    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent agent;

    public LogEntry(String logLine) {
        String[] parts = parseLogLine(logLine);
        this.ipAddr = parts[0];
        this.time = parseDateTime(parts[3]);
        String[] requestParts = parts[4].split(" ", 3);
        this.method = HttpMethod.fromString(requestParts[0]);
        this.path = requestParts.length > 1 ? requestParts[1] : "";
        this.responseCode = Integer.parseInt(parts[5]);
        this.responseSize = Integer.parseInt(parts[6]);
        this.referer = parts[7].equals("-") ? null : parts[7];
        this.agent = new UserAgent(parts[8]);
    }

    private String[] parseLogLine(String line) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean inBrackets = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            } else if (c == '[') {
                inBrackets = true;
                continue;
            } else if (c == ']') {
                inBrackets = false;
                continue;
            }

            if (c == ' ' && !inQuotes && !inBrackets) {
                if (current.length() > 0) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            parts.add(current.toString());
        }

        return parts.toArray(new String[0]);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.equals("-") || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка парсинга даты: '" + dateTimeStr + "'");
            return null;
        }
    }


    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getAgent() {
        return agent;
    }
}
