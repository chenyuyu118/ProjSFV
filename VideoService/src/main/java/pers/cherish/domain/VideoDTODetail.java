package pers.cherish.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Deprecated
public class VideoDTODetail {
    // 视频名称
    private String videoName;
    // 作者ID
    private String authorId;
    // 提交时间
    private String submitTime;
    // 是否发布
    private Boolean isPublished;
    // 是否删除
    private Boolean isDeleted;
    // tags
    private String tags;
    // 视频描述
    private String description;
    // 视频评论id
    private String commentId;
}
