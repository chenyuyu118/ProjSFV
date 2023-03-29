package pers.cherish.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.cherish.domain.VideoDTOUpdate;
import pers.cherish.videoservice.model.Video;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    void deleteVideoById(String videoId);

    List<Video> searchVideo(String key);

    List<Video> searchVideoPage(String key, int startIndex, int endIndex);

    Video getVideoById(String videoId);

    void updateVideo(VideoDTOUpdate video);

    boolean isVideoExist(String videoId);

    Video getRandomVideo();
}
