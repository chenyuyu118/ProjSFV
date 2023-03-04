package pers.cherish.userservice.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


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
    @NotNull(message="[头像id]不能为空")
    private String  id;
    /**
    * 注册者id
    */
    private Long ownerId;
    /**
    * 头像注册日期
    */
    @NotNull(message="[头像注册日期]不能为空")
    private Date registerTime;
}
