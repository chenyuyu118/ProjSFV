package pers.cherish.commentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private LocalDateTime commentTime;
    // 评论是否删除
    private Boolean isDeleted;
    // 回复id
    private Long replyId;

    private List<CommentTreeNode> child;


}
