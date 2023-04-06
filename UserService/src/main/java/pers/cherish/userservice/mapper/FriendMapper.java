package pers.cherish.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import pers.cherish.userservice.domain.Relation;
import pers.cherish.userservice.domain.UserBasicInfo;
import pers.cherish.userservice.model.UserVo;

import java.util.List;


@Mapper
public interface FriendMapper {
    List<UserVo> getFans(Long id);
    List<UserVo> getFollows(Long id);
    boolean follow(Long id, Long followId);
    boolean unfollow(Long id, Long followId);

    List<UserVo> getFriendList(Long id);
    List<UserVo> getFriendPage(Long id, int startIndex, int endIndex);

    void insertFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    List<UserVo> getFansPage(Long id, int startIndex, int endIndex);

    List<UserVo> getFollowsPage(Long id, int startIndex, int endIndex);

    UserBasicInfo getBasicInfo(long id);

    Long getFansCount(Long id);
    Long getFollowsCount(Long id);
    Long getFriendCount(Long id);

    List<Long> getFriendIdListForCache(Long id);

    List<Long> getFansIdListForCache(Long id);

    List<Long> getFollowsIdListForCache(Long id);

    Relation getRelationWith(long id, long otherId);
}
