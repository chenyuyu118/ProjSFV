package pers.cherish.commentservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "评论", example = "{" +
        "  \"videoId\": \"1\"," +
        "  \"authorId\": 1," +
        "  \"content\": \"评论内容\"," +
        "  \"parentId\": 0," +
        "  \"replyId\": 0" +
        "}")
public class CommentDTO {
    // 视频id
    private String videoId;
    // 评论作者
    private Long authorId;
    // 评论内容
    private String content;
    // 评论父评论id, 顶级评论为0
    private Long parentId;
    // 回复id
    private Long replyId;
}
