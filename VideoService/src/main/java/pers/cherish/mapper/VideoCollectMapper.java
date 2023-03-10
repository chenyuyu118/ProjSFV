package pers.cherish.mapper;

import org.apache.ibatis.annotations.Mapper;
import pers.cherish.domain.VideoVo;

import java.util.List;

@Mapper
public interface VideoCollectMapper {

    List<String> getCollectCategory(Long userId);

    List<VideoVo> getCollectionVideos(Long userId, String collector, int startIndex, int endIndex);

    void addCollect(Long userId, String videoId, String collector);

    void deleteCollect(Long userId, String videoId, String collector);

    void deleteCollector(Long userId, String collector);

    void updateCollector(Long userId, String collector, String newCollector);
}
