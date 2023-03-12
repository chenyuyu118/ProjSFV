package pers.cherish.userservice.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
* 
* @TableName t_profile
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_profile")
public class Profile implements Serializable {

    /**
    * 头像id
    */
    private String  id;
    /**
    * 注册者id
    */
    private Long ownerId;
    /**
    * 头像注册日期
    */
    private Date registerTime;
}
