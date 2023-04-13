package pers.cherish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.domain.VideoVo;
import pers.cherish.response.MyResponse;
import pers.cherish.service.VideoLikeService;
import pers.cherish.service.VideoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/video/like")
@Tags({
        @Tag(name = "Video", description = "视频点赞相关接口"),
        @Tag(name = "VideoLike", description = "视频点赞相关接口")}
)
public class VideoLikeController {

    @Value("${variable.page.video-like-page-size}")
    private int videoLikePageSize;
    @Value("${variable.redis.video-like-key-prefix}")
    private String videoLikeKeyPrefix;
    @Value("${variable.redis.video-dislike-key-prefix}")
    private String videoDislikeKeyPrefix;

    private VideoLikeService videoLikeService;
    private VideoService videoService;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Autowired
    public void setVideoLikeService(VideoLikeService videoLikeService) {
        this.videoLikeService = videoLikeService;
    }

    // 点赞
    @PostMapping("/{userId}")
    @PermissionConfirm
    @Operation(summary = "点赞")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "点赞成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "点赞失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> like(@PathVariable Long userId, @RequestBody String videoId) {
        if (!videoService.isVideoExist(videoId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("视频不存在"));
        }
        videoLikeService.like(userId, videoId);
        String key = videoLikeKeyPrefix + userId;
        stringRedisTemplate.opsForZSet().add(key, videoId, System.currentTimeMillis());
        return ResponseEntity.ok(MyResponse.ofMessage("点赞成功"));
    }

    // 取消点赞
    @DeleteMapping("/{userId}")
    @PermissionConfirm
    @Operation(summary = "取消点赞")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消点赞成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "取消点赞失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> cancelLike(@PathVariable Long userId, @RequestBody String videoId) {
        videoLikeService.cancelLike(userId, videoId);
        String key = videoLikeKeyPrefix + userId;
        stringRedisTemplate.opsForZSet().remove(key, videoId);
        return ResponseEntity.ok(MyResponse.ofMessage("取消点赞成功"));
    }

    // 获取视频点赞数
    @GetMapping("/count/{videoId}")
    @Operation(summary = "获取视频点赞数")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取视频点赞数成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取视频点赞数失败")
            }
    )
    public ResponseEntity<MyResponse<Long>> getLikeCount(@PathVariable String videoId) {
        return ResponseEntity.ok(MyResponse.
                ofData(videoLikeService.getLikeCount(videoId)));
    }

    // 获取用户是否点赞
    @GetMapping("/test/{userId}/{videoId}")
    @PermissionConfirm
    @Operation(summary = "获取用户是否点赞")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户是否点赞成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取用户是否点赞失败")
            }
    )
    public ResponseEntity<MyResponse<Boolean>> isLike(@PathVariable Long userId, @PathVariable String videoId) {
        return ResponseEntity.ok(MyResponse.
                ofData(videoLikeService.isLike(userId, videoId)));
    }

    // 获取用户是否点踩
    @GetMapping("dislike/test/{userId}/{videoId}")
    @PermissionConfirm
    @Operation(summary = "获取用户是否点踩")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户是否点赞成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取用户是否点赞失败")
            }
    )
    public ResponseEntity<MyResponse<Boolean>> isDisLike(@PathVariable long userId, @PathVariable String videoId) {
        return ResponseEntity.ok(MyResponse.
                ofData(videoLikeService.isDislike(userId, videoId)));
    }


    // 获取用户点赞的第k页视频
    @GetMapping("/{userId}/{k}")
    @Operation(summary = "获取用户点赞的第k页视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户点赞的第k页视频成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取用户点赞的第k页视频失败")
            }
    )
    public ResponseEntity<MyResponse<List<VideoVo>>> getLikeVideo(@PathVariable Long userId, @PathVariable int k) {
        return ResponseEntity.ok(MyResponse.
                ofData(videoLikeService.getLikeVideo(userId, k)));
    }

    // 获取用户点踩的第k页视频
    @GetMapping("/dislike/{userId}/{k}")
    @PermissionConfirm
    @Operation(summary = "获取用户点踩的第k页视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户点踩的第k页视频成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取用户点踩的第k页视频失败")
            }
    )
    public ResponseEntity<MyResponse<List<VideoVo>>> getDislikeVideo(@PathVariable Long userId, @PathVariable int k) {
        // TODO 这里结合redis缓存来做
//        String key = videoDislikeKeyPrefix + userId;
//        if (stringRedisTemplate.hasKey(key)) {
//            int startIndex = (k - 1) * videoLikePageSize;
//            int endIndex = k * videoLikePageSize;
//            Set<String> rangeIds = stringRedisTemplate.opsForZSet().range(key, startIndex, endIndex);
//        }
        return ResponseEntity.ok(MyResponse.
                ofData(videoLikeService.getDislikeVideo(userId, k)));
    }

    // 点踩
    @PostMapping("/dislike/{userId}")
    @PermissionConfirm
    @Operation(summary = "点踩")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "点踩成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "点踩失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> dislike(@PathVariable Long userId, @RequestBody String videoId) {
        if (!videoService.isVideoExist(videoId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("视频不存在"));
        }
        videoLikeService.dislike(userId, videoId);
        String key = videoDislikeKeyPrefix + userId;
        stringRedisTemplate.opsForZSet().add(key, videoId, System.currentTimeMillis());
        return ResponseEntity.ok(MyResponse.ofMessage("点踩成功"));
    }

    // 取消点踩
    @DeleteMapping("/dislike/{userId}")
    @PermissionConfirm
    @Operation(summary = "取消点踩")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消点踩成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "取消点踩失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> cancelDislike(@PathVariable Long userId, @RequestBody String videoId) {
        videoLikeService.cancelDislike(userId, videoId);
        String key = videoDislikeKeyPrefix + userId;
        stringRedisTemplate.opsForZSet().remove(key, videoId);
        return ResponseEntity.ok(MyResponse.ofMessage("取消点踩成功"));
    }

    private RedisScript<List> getIsLike;
    private RedisScript<List> getIsDislike;

    @Autowired
    @Qualifier("getIsLike")
    public void setGetIsLike(RedisScript getIsLike) {
        this.getIsLike = getIsLike;
    }

    @Autowired
    @Qualifier("getIsDislike")
    public void setGetIsDislike(RedisScript getIsDislike) {
        this.getIsDislike = getIsDislike;
    }


    // 获取一批视频是否点赞
    @GetMapping("/test/{userId}")
    @PermissionConfirm
    public ResponseEntity<MyResponse<List<Boolean>>> getIsLike(@RequestParam("ids") ArrayList<String> ids,
                                                               @PathVariable long userId) {
        String key = videoLikeKeyPrefix + userId;
        ArrayList<Boolean> result = new ArrayList<>();
        if (stringRedisTemplate.hasKey(key)) {
            List list = stringRedisTemplate.execute(getIsLike, List.of(key, key+":tmp", key+":tmp2"), ids.toArray());
            for (int i = 0; i < ids.size(); ++i) {
                if (list.contains(ids.get(i))) {
                    result.add(i, true);
                } else {
                    result.add(i, false);
                }
            }
        } else {
            result = videoLikeService.getIsLike(userId, ids);
        }
        return ResponseEntity.ok(MyResponse.ofData(result));
    }

    // 获取一批视频是否点踩
    @GetMapping("/dislike/test/{userId}")
    public ResponseEntity<MyResponse<List<Boolean>>> getIsDislike(@RequestParam("ids") ArrayList<String> ids,
                                                                  @PathVariable long userId) {
        String key = videoDislikeKeyPrefix + userId;
        ArrayList<Boolean> result = new ArrayList<>();
        if (stringRedisTemplate.hasKey(key)) {
            List list = stringRedisTemplate.execute(getIsLike, List.of(key, key+":tmp", key+":tmp1"), ids.toArray());
            for (int i = 0; i < ids.size(); ++i) {
                if (list.contains(ids.get(i))) {
                    result.add(i, true);
                } else {
                    result.add(i, false);
                }
            }
        } else {
            result = videoLikeService.getIsDisLike(userId, ids);
        }
        return ResponseEntity.ok(MyResponse.ofData(result));
    }


}
