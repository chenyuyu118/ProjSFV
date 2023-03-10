package pers.cherish.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(name = "VideoDTO", description = "视频信息")
public class VideoDTO {
    // 视频名称
    @Schema(description = "视频名称", example = "视频名称")
    private String videoName;
    // 作者ID
    @Schema(description = "作者ID", example = "1", type = "integer", format = "int64")
    private Long authorId;
    // 提交时间
    @Schema(description = "提交时间", example = "2021-01-01 00:00:00")
    private LocalDateTime submitTime;
    // 是否发布
    @Schema(description = "是否发布", example = "true")
    private Boolean isPublished;
    // 是否删除
    @Schema(description = "是否删除", example = "false")
    private Boolean isDeleted;
    // tags
    @Schema(description = "tags", example = "#tags")
    private String tags;
    // 视频描述
    @Schema(description = "视频描述", example = "视频描述")
    private String description;
    // 视频封面
    @Schema(description = "视频封面", example = "视频封面")
    private String profile;
}
