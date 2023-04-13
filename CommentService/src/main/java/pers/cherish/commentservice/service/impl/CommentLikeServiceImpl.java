package pers.cherish.commentservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.cherish.commentservice.mapper.CommentDislikeMapper;
import pers.cherish.commentservice.mapper.CommentLikeMapper;
import pers.cherish.commentservice.service.CommentLikeService;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private CommentLikeMapper commentLikeMapper;
    private CommentDislikeMapper commentDislikeMapper;

    @Autowired
    public void setCommentLikeMapper(CommentLikeMapper commentLikeMapper) {
        this.commentLikeMapper = commentLikeMapper;
    }

    @Autowired
    public void setCommentDislikeMapper(CommentDislikeMapper commentDislikeMapper) {
        this.commentDislikeMapper = commentDislikeMapper;
    }

    @Override
    public void likeComment(String videoId, Long commentId, Long userId) {
//        if (commentDislikeMapper.isDislikeComment(videoId, commentId, userId)) {
//            commentDislikeMapper.cancelDislike(videoId, commentId, userId);
//        }
        if (commentLikeMapper.isLikeComment(videoId, commentId, userId))
            return;
        commentLikeMapper.like(videoId, commentId, userId);
    }

    @Override
    public void cancelLikeComment(String videoId, Long commentId, Long userId) {
        if (!commentLikeMapper.isLikeComment(videoId, commentId, userId)) {
            return;
        }
        commentLikeMapper.cancelLike(videoId, commentId, userId);
    }

    @Override
    public void dislikeComment(String videoId, Long commentId, Long userId) {
        // TODO 点踩取消点赞
//        if (commentLikeMapper.isLikeComment(videoId, commentId, userId))
//            commentLikeMapper.cancelLike(videoId, commentId, userId);
        if (commentDislikeMapper.isDislikeComment(videoId, commentId, userId))
            return;
        commentDislikeMapper.dislike(videoId, commentId, userId);
    }

    @Override
    public void cancelDislikeComment(String videoId, Long commentId, Long userId) {
        if (!commentDislikeMapper.isDislikeComment(videoId, commentId, userId)) {
            return;
        }
        commentDislikeMapper.cancelDislike(videoId, commentId, userId);
    }

    @Override
    public boolean isLikeComment(String videoId, Long commentId, Long userId) {
        return commentLikeMapper.isLikeComment(videoId, commentId, userId);
    }

    @Override
    public boolean isDislikeComment(String videoId, Long commentId, Long userId) {
        return commentDislikeMapper.isDislikeComment(videoId, commentId, userId);
    }
}
