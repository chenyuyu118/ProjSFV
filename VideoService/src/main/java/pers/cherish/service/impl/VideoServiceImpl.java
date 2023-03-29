package pers.cherish.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.cherish.commons.cos.COSTemplate;
import pers.cherish.domain.VideoDTO;
import pers.cherish.domain.VideoDTOUpdate;
import pers.cherish.mapper.VideoMapper;
import pers.cherish.service.VideoService;
import pers.cherish.videoservice.model.Video;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    private VideoMapper videoMapper;
    private StringRedisTemplate stringRedisTemplate;

    private COSTemplate cosTemplate;

    @Autowired
    public void setCosTemplate(COSTemplate cosTemplate) {
        this.cosTemplate = cosTemplate;
    }

    // redis中视频计数器的key
    @Value("${variable.redis.video-counter-key}")
    private String videoCounterKey;

    @Value("${variable.page.video-page-size}")
    private Integer videoPageSize;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setVideoMapper(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    @Override
    public String uploadVideo(VideoDTO videoDTO) {
        videoDTO.setSubmitTime(LocalDateTime.now());
        Video newVideo = new Video();
        BeanUtils.copyProperties(videoDTO, newVideo);
        // TODO id生成需要更新
        final Long videoId = stringRedisTemplate.opsForValue().increment(videoCounterKey);
        newVideo.setVideoId(String.valueOf(videoId));
        final String imageUrl = cosTemplate.uploadImage(videoDTO.getProfile());
        newVideo.setProfileUrl(imageUrl);
        videoMapper.insert(newVideo);
        String key = videoId + ".mp4";
        newVideo.setVideoUrl(key);
        return cosTemplate.generatePreSignedUrl(key);
    }

    @Override
    public void deleteVideo(String videoId) {
        videoMapper.deleteVideoById(videoId);
    }


    @Override
    public void updateVideoInfo(VideoDTOUpdate videoDTOUpdate) {
        if (videoDTOUpdate.getProfile() == null) {
            videoMapper.updateVideo(videoDTOUpdate);
        } else {
            final String imageUrl = cosTemplate.uploadImage(videoDTOUpdate.getProfile());
            videoDTOUpdate.setProfile(imageUrl);
            videoMapper.updateVideo(videoDTOUpdate);
        }
    }

    @Override
    public String updateVideoContent(String videoId) {
        String key = videoId + ".mp4";
        return cosTemplate.generatePreSignedUrl(key);
    }


    @Override
    public List<Video> searchVideo(String key) {
        return videoMapper.searchVideo(key);
    }

    @Override
    public List<Video> searchVideo(String key, int k) {
        int startIndex = (k - 1) * videoPageSize;
        int endIndex = k * videoPageSize;
        return videoMapper.searchVideoPage(key, startIndex, endIndex);
    }

    @Override
    public Video getVideoById(String videoId) {
        return videoMapper.getVideoById(videoId);
    }

    @Override
    public boolean isVideoExist(String videoId) {
        return videoMapper.isVideoExist(videoId);
    }

    @Override
    public List<Video> getRandomVideo() {
        final ArrayList<Video> list = new ArrayList<>();
        Video randomVideo = videoMapper.getRandomVideo();
        list.add(randomVideo);
        randomVideo = videoMapper.getRandomVideo();
        list.add(randomVideo);
        return list;
    }
}
