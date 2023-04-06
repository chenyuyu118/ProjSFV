package pers.cherish.userservice.domain;

import lombok.Data;

@Data
public class UserBasicInfo {
    private Long id;
    private String profile;
    private String userName;
    private String signature;
    private boolean gender;
    private String currentAddress;
    private long followsCount;
    private long fansCount;
    private long likeCount;
}
