package pers.cherish.response;

import lombok.Data;

@Data
public class MyResponse<T> {
    T data;
    String message;

    public MyResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public static<T> MyResponse<T> ofData(T data) {
        return new MyResponse<>(data, null);
    }

    public static<T> MyResponse<T> ofMessage(String message) {
        return new MyResponse<>(null, message);
    }
}
