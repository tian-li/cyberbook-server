package app.cyberbook.cyberbookserver.model;

public class Const {
    public static final String ISOFormat = "dd/MM/YYYY'T'HH:mm:ss.SSS'Z'";

    public enum TransactionTypes {
        SPEND(0, "spend"),
        INCOME(1, "income");

        private final String value;
        private final int code;

        TransactionTypes(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static TransactionTypes codeOf(int code) {
            for (TransactionTypes transactionTypes : values()) {
                if (transactionTypes.getCode() == code) {
                    return transactionTypes;
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