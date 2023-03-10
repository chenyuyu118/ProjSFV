package pers.cherish.commentserive;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    // 评论视频id，同视频id
    private String videoId;
    // 评论id
    private Integer commentId;
    // 评论作者
    private Long authorId;
    // 评论内容
    private String content;
    // 点赞数目
    private Long likeCount;
    // 点踩数目
    private Long dislikeCount;
    // 评论时间
    private LocalDateTime commentTime;
    // 评论是否删除
    private Boolean isDeleted;
    // 评论是否被举报
    private Boolean isReported;
    // 评论父评论id, 顶级评论为0
    private Integer parentId;
    // 回复id
    private Integer replyId;
}