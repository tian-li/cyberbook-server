package app.cyberbook.cyberbookserver.model;

public class Const {
    public static final String ISOFormat = "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";

    public enum TransactionType {
        SPEND(0, "spend"),
        INCOME(1, "income");

        private final String value;
        private final int code;

        TransactionType(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static TransactionType codeOf(int code) {
            for (TransactionType transactionType : values()) {
                if (transactionType.getCode() == code) {
                    return transactionType;
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
}