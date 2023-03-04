package pers.cherish.userservice.model;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class User   {
    private Long id = null;
    private String userName = null;
    private String signature = null;
    private String phoneNumber = null;

    private Boolean gender = null;

    private String email = null;
    private String qqNumber = null;

    private String vxNumber = null;

    private Integer ipAddress = null;

    private String  profile = null;

    private String hometown = null;
    private String graduatedSchool = null;
    private Timestamp registerTime;

    private String verifyQuestion;
    private Date birthday;
    private String currentAddress;
    private boolean isDeleted;
    private boolean isBanned;
    private String password;
}