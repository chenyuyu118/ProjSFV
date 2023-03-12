package pers.cherish.commentservice.service;

public interface CommentLikeService {
    // 点赞评论
    void likeComment(String videoId, Long commentId, Long userId);
    // 取消点赞评论
    void cancelLikeComment(String videoId, Long commentId, Long userId);
    // 点踩评论
    void dislikeComment(String videoId, Long commentId, Long userId);
    // 取消点踩评论
    void cancelDislikeComment(String videoId, Long commentId, Long userId);
    // 获取是否点赞评论
    boolean isLikeComment(String videoId, Long commentId, Long userId);
    // 获取是否点踩评论
    boolean isDislikeComment(String videoId, Long commentId, Long userId);
}
