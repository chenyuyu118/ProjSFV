package pers.cherish.mapper;

import org.apache.ibatis.annotations.Mapper;
import pers.cherish.domain.VideoVo;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoLikeMapper {

    void like(Long userId, String videoId, LocalDateTime time);

    void cancelLike(Long userId, String videoId);

    Long getLkeCount(String videoId);

    boolean isLike(Long userId, String videoId);
    boolean isDislike(long userId, String videoId);


    List<VideoVo> getLikeVideo(Long userId, int startIndex, int endIndex);

    List<VideoVo> getDislikeVideo(Long userId, int startIndex, int endIndex);

    void dislike(Long userId, String videoId, LocalDateTime time);

    void cancelDislike(Long userId, String videoId);
}
