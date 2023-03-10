package pers.cherish.service;

import pers.cherish.domain.VideoDTO;
import pers.cherish.domain.VideoDTOUpdate;
import pers.cherish.videoservice.model.Video;

import java.util.List;

public interface VideoService {

    // 上传视频，返回签名
    String uploadVideo(VideoDTO videoDTO);

    // 删除视频
    void deleteVideo(String videoId);

    void updateVideoInfo(VideoDTOUpdate videoDTOUpdate);
    // 更新视频内容
    String updateVideoContent(String videoId);
    // 搜索视频
    List<Video> searchVideo(String key);
    // 搜索视频k页
    List<Video> searchVideo(String key, int k);

    // 通过视频id获取视频信息
    Video getVideoById(String videoId);

    boolean isVideoExist(String videoId);
}
