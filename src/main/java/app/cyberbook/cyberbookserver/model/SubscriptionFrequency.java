package app.cyberbook.cyberbookserver.model;

public enum SubscriptionFrequency {
    DAY(1, "day"),
    WEEK(2, "week"),
    MONTH(3, "month"),
    YEAR(4, "year");

    private final String value;
    private final int code;

    SubscriptionFrequency(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }
}
