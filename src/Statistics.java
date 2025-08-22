import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    //Курсовой проект. Задание #1 по теме "Collections"
    private Set<String> existingPages;
    private Map<String, Integer> osCounts;
    //Курсовой проект. Задание #2 по теме "Collections"
    private Set<String> notFoundPages;
    private Map<String, Integer> browserCounts;
    //Курсовой проект. Задание #1 по теме "Stream API"
    private List<LogEntry> allEntries = new ArrayList<>();

    public Statistics() {
        this.totalTraffic = 0L;
        this.minTime = null;
        this.maxTime = null;
        //Курсовой проект. Задание #1 по теме "Collections"
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
        //Курсовой проект. Задание #2 по теме "Collections"
        this.notFoundPages = new HashSet<>();
        this.browserCounts = new HashMap<>();
        //Курсовой проект. Задание #1 по теме "Stream API"
        this.allEntries = new ArrayList<>();
    }

    public void addEntry(LogEntry entry) {
        LocalDateTime entryTime = entry.getTime();
        if (entryTime == null) {
            return;
        }

//Курсовой проект. Задание #1 по теме "Stream API"
        allEntries.add(entry);

        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
        int responseSize = entry.getResponseSize();
        if (responseSize >= 0) {
            this.totalTraffic += responseSize;
        }
        //Курсовой проект. Задание #1 по теме "Collections"
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }
        String os = entry.getAgent().getOs();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
        //Курсовой проект. Задание #2 по теме "Collections"
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }
        String browser = entry.getAgent().getBrowser();
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);
    }

    //Курсовой проект. Задание #2 по теме "Stream API"
    private String extractDomain(String referer) {
        if (referer == null || referer.isEmpty() || referer.equals("-") || referer.equals("\"-\"")) {
            return null;
        }

        String decodeUrl;
        try {
            decodeUrl = URLDecoder.decode(referer, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            decodeUrl = referer;
        }

        String cleanUrl = decodeUrl.replace("\"", "").trim();
        if (cleanUrl.isEmpty() || cleanUrl.equals("-")) {
            return null;
        }

        try {
            String urlWithProtocol = cleanUrl;
            if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
                urlWithProtocol = "https://" + cleanUrl;
            }
            URI uri = new URI(urlWithProtocol);
            String host = uri.getHost();
            if (host != null) {
                return host;
            }
        } catch (URISyntaxException e) {
        }
        return extractDomainManually(cleanUrl);
    }

    private String extractDomainManually(String url) {

        String withoutProtocol = url;
        if (url.startsWith("http://")) {
            withoutProtocol = url.substring(7);
        } else if (url.startsWith("https://")) {
            withoutProtocol = url.substring(8);
        }

        int slashIndex = withoutProtocol.indexOf('/');
        if (slashIndex != -1) {
            withoutProtocol = withoutProtocol.substring(0, slashIndex);
        }

        int questionIndex = withoutProtocol.indexOf('?');
        if (questionIndex != -1) {
            withoutProtocol = withoutProtocol.substring(0, questionIndex);
        }

        int hashIndex = withoutProtocol.indexOf('#');
        if (hashIndex != -1) {
            withoutProtocol = withoutProtocol.substring(0, hashIndex);
        }

        int colonIndex = withoutProtocol.indexOf(':');
        if (colonIndex != -1) {
            withoutProtocol = withoutProtocol.substring(0, colonIndex);
        }

        if (withoutProtocol.isEmpty() ||
                withoutProtocol.equals("-") ||
                withoutProtocol.contains(" ") ||
                withoutProtocol.length() > 253) {
            return null;
        }

        return withoutProtocol;
    }

    public int getPeakVisitsPerSecond() {
        return allEntries.stream()
                .filter(entry -> !entry.getAgent().isBot())
                .map(entry -> entry.getTime())
                .filter(time -> time != null)
                .map(time -> time.withNano(0))
                .collect(Collectors.groupingBy(
                        time -> time,
                        Collectors.counting()
                ))
                .values().stream()
                .mapToInt(count -> count.intValue())
                .max()
                .orElse(0);
    }

    public HashSet<String> getRefererDomains() {
        return allEntries.stream()
                .map(entry -> entry.getReferer())
                .filter(referer -> referer != null)
                .filter(referer -> !referer.equals("-"))
                .filter(referer -> !referer.isEmpty())
                .map(referer -> extractDomain(referer))
                .filter(domain -> domain != null)
                .filter(domain -> !domain.isEmpty())
                .collect(Collectors.toCollection(() -> new HashSet<>()));
    }

    public int getMaxVisitsByUser() {
        return allEntries.stream()
                .filter(entry -> !entry.getAgent().isBot())
                .collect(Collectors.groupingBy(
                        entry -> entry.getIpAddr(), Collectors.counting()
                ))
                .values().stream()
                .mapToInt(count -> count.intValue())
                .max()
                .orElse(0);
    }


    //Курсовой проект. Задание #1 по теме "Collections"
    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStatistics = new HashMap<>();
        int totalCount = 0;
        for (int count : osCounts.values()) {
            totalCount += count;
        }
        if (totalCount > 0) {
            for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
                double share = (double) entry.getValue() / totalCount;
                osStatistics.put(entry.getKey(), share);
            }
        }
        return osStatistics;
    }

    //Курсовой проект. Задание #2 по теме "Collections"
    public Set<String> getNotFoundPages() {
        return new HashSet<>(notFoundPages);
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStatistics = new HashMap<>();
        int totalCount = 0;
        for (int count : browserCounts.values()) {
            totalCount += count;
        }
        if (totalCount > 0) {
            for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
                double share = (double) entry.getValue() / totalCount;
                browserStatistics.put(entry.getKey(), share);
            }
        }
        return browserStatistics;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0;
        }
        Duration duration = Duration.between(minTime, maxTime);
        double hoursBetween = duration.toMinutes() / 60.0;
        if (hoursBetween == 0) {
            return totalTraffic;
        }
        return (double) totalTraffic / hoursBetween;
    }

    public String getFormattedTrafficRate() {
        double rate = getTrafficRate();
        return String.format("%,.2f", rate) + " bytes/hour";
    }

    public String getFormattedTotalTraffic() {
        return String.format("%,d", totalTraffic) + " bytes";
    }

    public String getFormattedHoursBetween() {
        if (minTime == null || maxTime == null) {
            return "0.00";
        }
        Duration duration = Duration.between(minTime, maxTime);
        double hours = duration.toMinutes() / 60.0;
        return String.format("%.2f", hours);
    }

    //Курсовой проект. Задание #1 по теме "Stream API"
    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0;
        }
        long nonBotCount = allEntries.stream()
                .filter(entry -> !entry.getAgent().isBot())
                .count();
        Duration duration = Duration.between(minTime, maxTime);
        double hoursBetween = duration.toHours();
        if (hoursBetween == 0) {
            hoursBetween = 1;
        }
        return (double) nonBotCount / hoursBetween;
    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0;
        }
        long errorCount = allEntries.stream()
                .filter(entry -> entry.getResponseCode() >= 400 && entry.getResponseCode() < 600)
                .count();
        Duration duration = Duration.between(minTime, maxTime);
        double hoursBetween = duration.toHours();
        if (hoursBetween == 0) {
            hoursBetween = 1;
        }
        return (double) errorCount / hoursBetween;
    }

    public double getAverageVisitsPerUser() {
        long nonBotVisits = allEntries.stream()
                .filter(entry -> !entry.getAgent().isBot())
                .count();
        long uniqueNonBotIps = allEntries.stream()
                .filter(entry -> !entry.getAgent().isBot())
                .map(entry -> entry.getIpAddr())
                .distinct()
                .count();
        if (uniqueNonBotIps == 0) {
            return 0;
        }
        return (double) nonBotVisits / uniqueNonBotIps;
    }

    public String getFormattedAverageVisitsPerHour() {
        return String.format("%,.2f", getAverageVisitsPerHour());
    }

    public String getFormattedAverageErrorRequestsPerHour() {
        return String.format("%,.2f", getAverageErrorRequestsPerHour());
    }

    public String getFormattedAverageVisitsPerUser() {
        return String.format("%,.2f", getAverageVisitsPerUser());
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

}