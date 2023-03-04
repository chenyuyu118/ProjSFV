package pers.cherish.userservice.domain;

import org.springframework.data.web.JsonPath;
import pers.cherish.userservice.model.UserVo;

public interface FriendProjection {
    @JsonPath("$.id")
    Long getId();

    @JsonPath("$.user")
    UserVo getUser();

    @JsonPath("$.otherId")
    Long getOtherId();
}
