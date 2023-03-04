package pers.cherish.userservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private String signature;
    private String password;
    private String phoneNumber;
    /**
     * 男生为1,女生为0
      */
    private boolean sex;
    private String email;
    private String qqNumber;
    private String vxNumber;
    private int ipAddress;
    private Date birthday;
    private String profile;
    private String currentAddress;
    private String hometown;
    private String  graduatedSchool;
}
