import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    //Курсовой проект. Задание #1 по теме "Collections"
    private Set<String> existingPages;
    private Map<String, Integer> osCounts;

    public Statistics() {
        this.totalTraffic = 0L;
        this.minTime = null;
        this.maxTime = null;
        //Курсовой проект. Задание #1 по теме "Collections"
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        LocalDateTime entryTime = entry.getTime();
        if (entryTime == null) {
            return;
        }
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
        if (entry.getResponseCode() == 200){
            existingPages.add(entry.getPath());
        }
        String os = entry.getAgent().getOs();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
    }

    //Курсовой проект. Задание #1 по теме "Collections"
    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStatistics = new HashMap<>();
        int totalCount = 0;
        for (int count: osCounts.values()) {
            totalCount += count;
        }
        if (totalCount > 0){
            for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
                double share = (double) entry.getValue() / totalCount;
                osStatistics.put(entry.getKey(), share);
            }
        }
        return osStatistics;
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

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

}