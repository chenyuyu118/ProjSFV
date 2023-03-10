package pers.cherish.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Schema(name = "VideoDTOUpdate", description = "视频信息", example = "{\n" +
        "  \"videoId\": 0,\n" +
        "  \"videoName\": \"string\",\n" +
        "  \"authorId\": 0,\n" +
        "  \"isPublished\": true,\n" +
        "  \"tags\": \"string\",\n" +
        "  \"description\": \"string\",\n" +
        "  \"profile\": \"string\"\n" +
        "}")
public class VideoDTOUpdate {
    private Long videoId;
    private String videoName;
    private String profile;
    // 是否发布
    private Boolean isPublished;
    // tags
    private String tags;
    // 视频描述
    private String description;
    private Long authorId;
}
