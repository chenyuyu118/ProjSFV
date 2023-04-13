package pers.cherish.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import pers.cherish.domain.VideoVo;
import pers.cherish.mapper.VideoCollectMapper;
import pers.cherish.service.VideoCollectService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoCollectServiceImpl implements VideoCollectService {

    private VideoCollectMapper videoCollectMapper;

    @Value("${variable.page.video-collect-page-size}")
    private int collectPageSize;

    @Autowired
    public void setVideoCollectMapper(VideoCollectMapper videoCollectMapper) {
        this.videoCollectMapper = videoCollectMapper;
    }

    @Override
    public List<String> getCollectCategory(Long userId) {
        return videoCollectMapper.getCollectCategory(userId);
    }

    @Override
    public List<VideoVo> getCollectVideo(Long userId, String category, int k) {
        int startIndex = (k - 1) * collectPageSize;
        int endIndex = k * collectPageSize;
        return videoCollectMapper.getCollectionVideos(userId, category, startIndex, endIndex);
    }

    @Override
    public void addCollect(Long userId, String videoId, String category) {
        videoCollectMapper.addCollect(userId, videoId, category);
    }

    @Override
    public void deleteCollect(Long userId, String videoId, String category) {
        videoCollectMapper.deleteCollect(userId, videoId, category);
    }

    @Override
    public void deleteCollector(Long userId, String category) {
        videoCollectMapper.deleteCollector(userId, category);
    }

    @Override
    public void updateCollector(Long userId, String category, String newCategory) {
        videoCollectMapper.updateCollector(userId, category, newCategory);
    }

    @Override
    public boolean isCollectVideo(long userId, String videoId) {
        return videoCollectMapper.isCollectVideo(userId, videoId);
    }

    @Override
    public List<Boolean> getIsCollect(Long userId, ArrayList<String> ids) {
        List<String> videoIds = videoCollectMapper.getAllCollectVideos(userId);
        ArrayList<Boolean> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (videoIds.contains(ids.get(i)))
                result.add(i, true);
            else
                result.add(i, false);
        }
        return result;
    }

    @Override
    public List<String> getAllCollectVideoIds(Long userId) {
        return videoCollectMapper.getAllCollectVideos(userId);
    }
}
