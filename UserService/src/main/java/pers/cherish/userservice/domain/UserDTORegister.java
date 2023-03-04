package pers.cherish.userservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户注册信息", description = "用户注册信息")
public class UserDTORegister {
    @Schema(name = "id", description = "用户id", example = "1", type = "integer", format = "int64", hidden = true)
    private Long id;
    @Schema(name = "userName", description = "用户名", example = "cherish")
    private String userName;
    @Schema(name = "signature", description = "签名", example = "我是签名")
    private String signature;
    @Schema(name = "password", description = "密码", example = "123456")
    private String password;
    @Schema(name = "phoneNumber", description = "昵称", example = "cherish")
    private String phoneNumber;
    /**
     * 男生为1,女生为0
     */
    @Schema(name = "gender", description = "性别", example = "true", type = "boolean", allowableValues = {"true", "false"})
    private boolean gender;
    @Schema(name = "email", description = "邮箱", example = "1@qq.com")
    private String email;
    @Schema(name = "qqNumber", description = "qq号", example = "123456789")
    private String qqNumber;
    @Schema(name = "vxNumber", description = "vx号", example = "123456789")
    private String vxNumber;
    @Schema(name = "verifyQuestion", description = "验证问题", example = "我是验证问题")
    private String verifyQuestion;
    @Schema(name = "ipAddress", description = "ip地址", example = "0xffffffff", type = "integer")
    private int ipAddress;
    @Schema(name = "profile", description = "头像", example = "jpg,123123", type = "String", format = "base64")
    private String  profile;
    @Schema(name = "currentAddress", description = "现居地", example = "北京市海淀区")
    private String currentAddress;
    @Schema(name = "hometown", description = "家乡", example = "北京市海淀区")
    private String hometown;
    @Schema(name = "graduatedSchool", description = "毕业学校", example = "北京大学")
    private String  graduatedSchool;
    @Schema(name = "birthday", description = "生日", example = "2020-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @Schema(name = "profileId", description = "头像id", example = "1", type = "string", format = "uuid", hidden = true)
    private String profileId;
}
