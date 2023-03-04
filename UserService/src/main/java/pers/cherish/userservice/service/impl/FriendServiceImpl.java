package pers.cherish.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.cherish.userservice.domain.UserBasicInfo;
import pers.cherish.userservice.mapper.FriendBlockedMapper;
import pers.cherish.userservice.mapper.FriendMapper;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.FriendService;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {
    private final FriendMapper friendMapper;
    private final FriendBlockedMapper friendBlockedMapper;
    @Autowired
    public FriendServiceImpl(FriendMapper friendMapper, FriendBlockedMapper friendBlockedMapper) {
        this.friendMapper = friendMapper;
        this.friendBlockedMapper = friendBlockedMapper;
    }

    @Override
    public List<UserVo> getBlockedList(Long id, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        return friendBlockedMapper.getBlockedPage(id, startIndex, endIndex);
    }

    @Override
    public void addToBlockedList(Long id, long id1) {
        friendBlockedMapper.addToBlockedList(id, id1);
    }

    @Override
    public List<UserVo> getFans(Long id) {
        return friendMapper.getFans(id);
    }

    @Override
    public List<UserVo> getFans(Long id, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        return friendMapper.getFansPage(id, startIndex, endIndex);
    }

    @Override
    public List<UserVo> getFollows(Long id) {
        return friendMapper.getFollows(id);
    }

    @Override
    public List<UserVo> getFollows(Long id, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        return friendMapper.getFollowsPage(id, startIndex, endIndex);
    }

    @Override
    public void follow(Long id, Long followId) {
        friendMapper.follow(id, followId);
    }

    @Override
    public void unfollow(Long id, Long followId) {
        friendMapper.unfollow(id, followId);
    }
    @Override
    public List<UserVo> getBlockedList(Long id) {
        return friendBlockedMapper.getBlockedList(id);
    }

    @Override
    public void deleteFromBlockedList(Long id, Long blockedId) {
        friendBlockedMapper.deleteFromBlockedList(id, blockedId);
    }

    @Override
    public List<UserVo> getFriendList(Long id) {
        return friendMapper.getFriendList(id);
    }

    @Override
    public List<UserVo> getFriendList(Long id, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        return friendMapper.getFriendPage(id, startIndex, endIndex);
    }

    public void relateWithOther(Long id, Long otherId, RelationMessage message) {
        if (message == RelationMessage.ACCEPT) {
            friendMapper.insertFriend(id, otherId);
        } else {
            friendMapper.deleteFriend(id, otherId);
        }
    }

    @Override
    public UserBasicInfo getBasicInfo(long id) {
        final UserBasicInfo info = friendMapper.getBasicInfo(id);
        info.setFansCount(friendMapper.getFansCount(id));
        info.setFollowsCount(friendMapper.getFollowsCount(id));
        return info;
    }

    @Override
    public List<Long> getFriendIdListForCache(Long id) {
        return friendMapper.getFriendIdListForCache(id);
    }

    @Override
    public List<Long> getFansIdListForCache(Long id) {
        return friendMapper.getFansIdListForCache(id);
    }

    @Override
    public List<Long> getFollowsIdListForCache(Long id) {
        return friendMapper.getFollowsIdListForCache(id);
    }

    @Override
    public List<Long> getBlockedListForCache(Long id) {
        return friendBlockedMapper.getBlockedIdListForCache(id);
    }


}
