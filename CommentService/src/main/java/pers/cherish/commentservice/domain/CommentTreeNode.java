package pers.cherish.commentservice.domain;

import lombok.Data;

import java.util.List;

@Data
public class CommentTreeNode {
    // 评论id
    private Long commentId;
    // 父评论id
    private Long parentId;
    // 评论内容
    private String content;
    // 评论作者
    private Long authorId;
    // 点赞数目
    private Long likeCount;
    // 评论时间
    private String commentTime;
    // 评论是否删除
    private Boolean isDeleted;
    // 回复id
    private Long replyId;

    private List<CommentTreeNode> child;
}
