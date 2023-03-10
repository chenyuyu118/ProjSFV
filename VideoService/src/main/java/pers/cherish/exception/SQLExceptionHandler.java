package pers.cherish.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pers.cherish.response.MyResponse;


@ControllerAdvice
public class SQLExceptionHandler {
    @ExceptionHandler(DuplicateKeyException.class)
    private ResponseEntity<MyResponse<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        System.out.println("---------------------------------------------");
        System.out.println(e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            System.out.println("已存在");
        }
        System.out.println("---------------------------------------------");
        return ResponseEntity.badRequest().body(MyResponse.ofMessage("已存在"));
    }
}
