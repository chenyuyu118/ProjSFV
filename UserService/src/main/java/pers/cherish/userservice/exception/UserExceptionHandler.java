package pers.cherish.userservice.exception;
@ControllerAdvice
public class UserExceptionHandler {
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    void handleDuplicateKeyException(SQLIntegrityConstraintViolationException e) {
////        System.out.println(e.getMessage());
//        System.out.println("1111111111111111111111111111111111111111111111111");
//        System.out.println(e.getSQLState());
//        if (e.getMessage().contains("Duplicate entry")) {
//            System.out.println("用户名已存在");
//        }
//    }

//    @ExceptionHandler(DuplicateKeyException.class)
//    private ResponseEntity<Map<String , String >> handleDuplicateKeyException(DuplicateKeyException e) {
//        System.out.println("---------------------------------------------");
//        System.out.println(e.getMessage());
//        if (e.getMessage().contains("Duplicate entry")) {
//            System.out.println("用户名已存在");
//        }
//        System.out.println("---------------------------------------------");
//        return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
//    }

//    @ExceptionHandler(Exception.class)
//    void handleException(Exception e) {
//        System.out.println("----------------------------------");
//        System.out.println(e.getClass().getName());
//        System.out.println(e.getMessage());
//        System.out.println("----------------------------------");
//    }
}
