package pers.cherish.videoservice.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("t_video")
@Data
@NoArgsConstructor
public class Video {
    // 视频id
    private String videoId;
    // 视频名称
    private String videoName;
    // 视频url
    private String videoUrl;
    // 视频作者id
    private Long authorId;
    // 视频长度，单位秒
    private Integer videoLength;
    // 点赞数目
    private Integer likeCount = 0;
    // 视频封面url
    private String profileUrl;
    // 点踩数目
    private Integer dislikeCount = 0;
    // 提交时间
    private LocalDateTime submitTime;
    // 是否发布
    private Boolean isPublished;
    // 是否删除
    private Boolean isDeleted;
    // 是否视频被禁止
    private Boolean isBanned = true;
    // 标签，中间以#分隔
    private String tags;
    // 视频描述
    private String description;

}