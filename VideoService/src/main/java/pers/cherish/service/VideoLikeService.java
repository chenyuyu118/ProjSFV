package pers.cherish.service;

import pers.cherish.domain.VideoVo;

import java.util.List;

public interface VideoLikeService {
    // 点赞
    void like(Long userId, String videoId);
    // 取消点赞
    void cancelLike(Long userId, String videoId);
    // 获取视频点赞数
    Long getLikeCount(String videoId);
    // 获取用户是否点赞
    boolean isLike(Long userId, String videoId);
    boolean isDislike(long userId, String videoId);

    // 获取用户点赞的第k页视频
    List<VideoVo> getLikeVideo(Long userId, int k);
    // 获取用户点踩的第k页视频
    List<VideoVo> getDislikeVideo(Long userId, int k);
    // 点踩
    void dislike(Long userId, String videoId);
    // 取消点踩
    void cancelDislike(Long userId, String videoId);
}
