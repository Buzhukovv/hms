package housingManagment.hms.enums;

public enum ReportSchedule {
    DAILY("0 0 0 * * ?"), // Every day at midnight
    WEEKLY("0 0 0 ? * MON"), // Every Monday at midnight
    MONTHLY("0 0 0 1 * ?"), // First day of each month at midnight
    QUARTERLY("0 0 0 1 1,4,7,10 ?"), // First day of each quarter at midnight
    SEMESTER("0 0 0 1 1,8 ?"); // First day of each semester (Jan and Aug) at midnight

    private final String cronExpression;

    ReportSchedule(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }
}