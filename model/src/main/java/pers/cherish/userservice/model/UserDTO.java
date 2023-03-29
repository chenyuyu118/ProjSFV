package pers.cherish.userservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private boolean gender;
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
