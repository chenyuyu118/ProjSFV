package pers.cherish.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pers.cherish.domain.VideoVo;
import pers.cherish.mapper.VideoLikeMapper;
import pers.cherish.service.VideoLikeService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoLikeServiceImpl implements VideoLikeService {

    private VideoLikeMapper videoLikeMapper;

    // 视频页大小
    @Value("${variable.page.video-like-page-size}")
    private int videoLikePageSize;


    @Autowired
    public void setVideoLikeMapper(VideoLikeMapper videoLikeMapper) {
        this.videoLikeMapper = videoLikeMapper;
    }

    @Override
    public void like(Long userId, String videoId) {
        videoLikeMapper.like(userId, videoId, LocalDateTime.now());
    }

    @Override
    public void cancelLike(Long userId, String videoId) {
        videoLikeMapper.cancelLike(userId, videoId);
    }

    @Override
    public Long getLikeCount(String videoId) {
        return videoLikeMapper.getLkeCount(videoId);
    }

    @Override
    public boolean isLike(Long userId, String videoId) {
        return videoLikeMapper.isLike(userId, videoId);
    }

    @Override
    public boolean isDislike(long userId, String videoId) {
        return videoLikeMapper.isDislike(userId, videoId);
    }

    @Override
    public List<VideoVo> getLikeVideo(Long userId, int k) {
        int startIndex = (k - 1) * videoLikePageSize;
        int endIndex = k * videoLikePageSize;
        return videoLikeMapper.getLikeVideo(userId, startIndex, endIndex);
    }

    @Override
    public List<VideoVo> getDislikeVideo(Long userId, int k) {
        int startIndex = (k - 1) * videoLikePageSize;
        int endIndex = k * videoLikePageSize;
        return videoLikeMapper.getDislikeVideo(userId, startIndex, endIndex);
    }

    @Override
    public void dislike(Long userId, String videoId) {
        videoLikeMapper.dislike(userId, videoId, LocalDateTime.now());
    }

    @Override
    public void cancelDislike(Long userId, String videoId) {
        videoLikeMapper.cancelDislike(userId, videoId);
    }
}
