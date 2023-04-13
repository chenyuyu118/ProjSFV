package pers.cherish.service;

import pers.cherish.domain.VideoVo;

import java.util.ArrayList;
import java.util.List;

public interface VideoCollectService {
    // 获得用户的所有收藏类目
    List<String> getCollectCategory(Long userId);
    // 获取用户某种收藏类目下的第k页视频
    List<VideoVo> getCollectVideo(Long userId, String category, int k);
    // 添加收藏
    void addCollect(Long userId, String videoId, String category);
    // 删除收藏
    void deleteCollect(Long userId, String videoId, String category);
    // 删除收藏类目
    void deleteCollector(Long userId, String category);
    // 修改收藏类目
    void updateCollector(Long userId, String category, String newCategory);

    boolean isCollectVideo(long userId, String videoId);

    List<Boolean> getIsCollect(Long userId, ArrayList<String> ids);

    List<String> getAllCollectVideoIds(Long userId);
}
