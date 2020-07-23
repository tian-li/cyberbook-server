package app.cyberbook.cyberbookserver.model;

public class Const {
    public enum TransactionType {
        SPEND(0, "spend"),
        INCOME(1, "income");

        private final String value;
        private final int code;

        TransactionType(int code, String value) {
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
}