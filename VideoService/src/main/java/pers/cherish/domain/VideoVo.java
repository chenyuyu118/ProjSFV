package pers.cherish.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoVo {
    // 视频id
    private String videoId;
    // 视频名称
    private String videoName;
    // 视频tags
    private String tags;
    // 封面url
    private String profileUrl;
}
