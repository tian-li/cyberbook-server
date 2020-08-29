package app.cyberbook.cyberbookserver.model;

import lombok.Getter;

public class CyberbookServerResponse<T> {
    @Getter
    private T data;

    @Getter
    private String message;

    CyberbookServerResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    CyberbookServerResponse(String message) {
        this.message = message;
    }

    public static <T> CyberbookServerResponse<T> successWithData(T data) {
        return new CyberbookServerResponse<T>(data, "Success");
    }

    public static <T> CyberbookServerResponse<T> failedWithData(T data) {
        return new CyberbookServerResponse<T>(data, "Failed");
    }

    public static <T> CyberbookServerResponse<T> failedNoData() {
        return new CyberbookServerResponse<T>("Failed");
    }

    public static <T> CyberbookServerResponse<T> noDataMessage(String message) {
        return new CyberbookServerResponse<T>(message);
    }
}
