package pers.cherish.userservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserFriendVo {
    private Long id;
    private String userName;
    private String signature;
    private boolean sex;
    private String ip;
    private String profile;
}
