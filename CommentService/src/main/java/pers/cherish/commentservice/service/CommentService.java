package pers.cherish.commentservice.service;

import pers.cherish.commentservice.Comment;
import pers.cherish.commentservice.domain.CommentTree;
import pers.cherish.commentservice.domain.CommentTreeNode;

import java.util.List;

public interface CommentService {
    // 发布评论

    void publishComment(String videoId, String content, Long userId,
                        Long parentId);

    void publishComment(String videoId, String content, Long userId,
                        Long parentId, Long replyId);
    // 删除评论
    void deleteComment(String  videoId, Long commentId, Long authorId);
    // 获取评论树第k页按热度排序
    CommentTree getCommentTree(String videoId, int k);
    // 获得具体评论下行树节点按热度排序
    List<CommentTreeNode> getCommentTree(String videoId, Long commentId, int k);
    // 获得评论树第k页按时间排序
    CommentTree getCommentTreeByTime(String videoId, int k);
    // 获得具体评论下行树节点按时间排序
    List<CommentTreeNode> getCommentTreeByTime(String videoId, Long commentId, int k);
    // 获得完整评论树
    CommentTree getCommentTree(String videoId);
    // 获得自己的评论
    List<Comment> getMyComments(Long userId);
    List<Comment> getMyComments(Long userId, int k);
    // 获得评论的追评
    List<CommentTreeNode> getCommentReply(String videoId, Long commentId);
    // 评论是否存在
    boolean isExist(String videoId, Long commentId);
}
