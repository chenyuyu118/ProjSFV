package pers.cherish.userservice.service;

import org.springframework.stereotype.Service;
import pers.cherish.userservice.domain.UserBasicInfo;
import pers.cherish.userservice.model.UserVo;

import java.util.List;

@Service
public interface FriendService {
    List<UserVo> getBlockedList(Long id, int page, int pageSize);

    void addToBlockedList(Long id, long id1);

    List<UserVo> getFans(Long id);

    List<UserVo> getFans(Long id, int page, int pageSize);

    List<UserVo> getFollows(Long id);

    List<UserVo> getFollows(Long id, int page, int pageSize);

    void follow(Long id, Long followId);

    void unfollow(Long id, Long followId);

    List<UserVo> getBlockedList(Long id);

    void deleteFromBlockedList(Long id, Long blockedId);

    List<UserVo> getFriendList(Long id);

    List<UserVo> getFriendList(Long id, int page, int pageSize);

    void relateWithOther(Long id, Long otherId, RelationMessage message);

    UserBasicInfo getBasicInfo(long id);

    List<Long> getFriendIdListForCache(Long id);

    List<Long> getFansIdListForCache(Long id);

    List<Long> getFollowsIdListForCache(Long id);

    List<Long> getBlockedListForCache(Long id);
//    enum FriendStatus {
//        FRIEND, FOLLOW, FANS, BLOCKED, OTHER
//    }
    enum RelationMessage {
        REQ, REJECT, ACCEPT, DELETE
    }

}
