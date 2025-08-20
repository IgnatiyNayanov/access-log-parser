import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0L;
        this.minTime = null;
        this.maxTime = null;
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