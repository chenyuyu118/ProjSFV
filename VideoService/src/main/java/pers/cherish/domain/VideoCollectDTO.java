package pers.cherish.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "视频收藏", example = "{\n" +
        "  \"videoId\": \"1\",\n" +
        "  \"category\": \"默认\",\n" +
        "  \"newCategory\": \"新类目\"\n" +
        "}")
public class VideoCollectDTO {
    private String videoId;
    private String category;
    private String newCategory;
}
