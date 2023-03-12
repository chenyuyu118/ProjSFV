package pers.cherish.commentservice.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDislikeMapper {

    void dislike(String videoId, Long commentId, Long userId);

    void cancelDislike(String videoId, Long commentId, Long userId);

    boolean isDislikeComment(String videoId, Long commentId, Long userId);
}
