package pers.cherish.commentservice.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeMapper {
    void like(String videoId, Long commentId, Long userId);

    void cancelLike(String videoId, Long commentId, Long userId);

    boolean isLikeComment(String videoId, Long commentId, Long userId);

    boolean isCommentExist(String videoId, Long commentId);
}
