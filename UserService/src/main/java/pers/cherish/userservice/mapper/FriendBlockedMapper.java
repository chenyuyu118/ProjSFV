package pers.cherish.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import pers.cherish.userservice.model.UserVo;

import java.util.List;

@Mapper
public interface FriendBlockedMapper {
    List<UserVo> getBlockedList(Long id);

    void deleteFromBlockedList(Long id, Long blockedId);

    void addToBlockedList(Long id, Long blockedId);

    List<UserVo> getBlockedPage(Long id, int startIndex, int endIndex);

    List<Long> getBlockedIdListForCache(Long id);

    boolean isBlockedFriend(long id, long otherId);
}
