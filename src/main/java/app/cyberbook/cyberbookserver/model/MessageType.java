package app.cyberbook.cyberbookserver.model;

public enum MessageType {
    SYSTEM(0, "System"),
    PRIVATE(1, "private");

    private final String value;
    private final int code;

    MessageType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static MessageType codeOf(int code) {
        for (MessageType messageType : values()) {
            if (messageType.getCode() == code) {
                return messageType;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }
}
