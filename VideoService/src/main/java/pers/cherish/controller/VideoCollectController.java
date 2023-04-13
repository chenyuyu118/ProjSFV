package pers.cherish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.domain.VideoCollectDTO;
import pers.cherish.domain.VideoVo;
import pers.cherish.response.MyResponse;
import pers.cherish.service.VideoCollectService;
import pers.cherish.service.VideoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/video/collect")
@Tags({@Tag(name = "Video", description = "视频收藏相关接口")})
public class VideoCollectController {

    private VideoCollectService videoCollectService;
    private VideoService videoService;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${variable.redis.video-collect-key-prefix}")
    private String videoCollectKeyPrefix;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Autowired
    public void setVideoCollectService(VideoCollectService videoCollectService) {
        this.videoCollectService = videoCollectService;
    }

    @GetMapping("/category/{userId}")
    @PermissionConfirm
    @Operation(summary = "获取用户收藏类目")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
            }
    )
    public ResponseEntity<MyResponse<List<String >>> getCollectCategory(@PathVariable Long userId) {
        return ResponseEntity.ok(MyResponse.ofData(videoCollectService.getCollectCategory(userId)));
    }

    // 获取用户某种收藏类目下的第k页视频
    @GetMapping("/{userId}/{category}/{k}")
    @Operation(summary = "获取用户某种收藏类目下的第k页视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
            }
    )
    @PermissionConfirm
    public ResponseEntity<MyResponse<List<VideoVo>>> getCollectVideo(@PathVariable Long userId,
                                                                     @PathVariable String category,
                                                                     @PathVariable(required = false) Integer k) {
        k = Objects.requireNonNullElse(k, 1);
        return ResponseEntity.ok(MyResponse.ofData(videoCollectService.getCollectVideo(userId, category, k)));
    }

    // 添加收藏
    @PostMapping("/{userId}")
    @PermissionConfirm
    @Operation(summary = "添加收藏")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "添加成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "添加失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> addCollect(@PathVariable Long userId,
            @RequestBody VideoCollectDTO videoDTO) {
        if (!videoService.isVideoExist(videoDTO.getVideoId())) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("视频不存在"));
        }
        videoCollectService.addCollect(userId, videoDTO.getVideoId(), videoDTO.getCategory());
        String key = videoCollectKeyPrefix + userId;
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForSet().isMember(key, videoDTO.getVideoId()))) {
            stringRedisTemplate.opsForSet().add(key, videoDTO.getVideoId());
        }
        return ResponseEntity.ok(MyResponse.ofMessage("添加收藏成功"));
    }

    // 删除收藏
    @DeleteMapping("/{userId}")
    @PermissionConfirm
    @Operation(summary = "删除收藏")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> deleteCollect(@PathVariable Long userId,
            @RequestBody VideoCollectDTO videoDTO) {
        videoCollectService.deleteCollect(userId, videoDTO.getVideoId(), videoDTO.getCategory());
        String key = videoCollectKeyPrefix + userId;
        stringRedisTemplate.opsForSet().remove(key, videoDTO.getVideoId());
        return ResponseEntity.ok(MyResponse.ofMessage("删除收藏成功"));
    }

    // 删除收藏类目
    @PermissionConfirm
    @DeleteMapping("/category/{userId}/{category}")
    @Operation(summary = "删除收藏类目")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> deleteCollector(@PathVariable Long userId, @PathVariable String category) {
        videoCollectService.deleteCollector(userId, category);
        String key = videoCollectKeyPrefix + userId;
        stringRedisTemplate.delete(key);
        List<String> ids = videoCollectService.getAllCollectVideoIds(userId);
        stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                for (int i = 0; i < ids.size(); i++) {
                    stringRedisConnection.sAdd(key, ids.get(i));
                }
                return null;
            }
        });
        return ResponseEntity.ok(MyResponse.ofMessage("删除收藏类目成功"));
    }

    // 修改收藏类目名称
    @PutMapping("/{userId}")
    @PermissionConfirm
    @Operation(summary = "修改收藏类目名称")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "修改失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> updateCollector(@PathVariable Long userId,
            @RequestBody VideoCollectDTO videoDTOUpdate) {
        videoCollectService.updateCollector(userId, videoDTOUpdate.getCategory(), videoDTOUpdate.getNewCategory());
        return ResponseEntity.ok(MyResponse.ofMessage("修改收藏类目名称成功"));
    }

    // 获取是否收藏
    @GetMapping("/test/{userId}/{videoId}")
    @PermissionConfirm
    public ResponseEntity<MyResponse<Boolean>> getIsCollect(@PathVariable Long userId,
                                                            @PathVariable String videoId) {
        return ResponseEntity.ok(MyResponse.ofData(videoCollectService.isCollectVideo(userId, videoId)));
    }

    RedisScript<List> isCollect;

    @Autowired
    @Qualifier("getIsCollect")
    public void setIsCollect(RedisScript<List> isCollect) {
        this.isCollect = isCollect;
    }

    // 获取一批视频是否收藏
    @GetMapping("/test/{userId}")
    @PermissionConfirm
    public ResponseEntity<MyResponse<List<Boolean>>> getIsCollectList(@RequestParam(name = "ids")ArrayList<String > ids,
        @PathVariable Long userId) {
        String key = videoCollectKeyPrefix + userId;
        List<Boolean> result;
        if (stringRedisTemplate.hasKey(key)) {
            List list = stringRedisTemplate.execute(isCollect, List.of(key, key + ":temp"), ids.toArray());
            result = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                result.add(i, list.contains(ids.get(i)));
            }
        } else {
            result = videoCollectService.getIsCollect(userId, ids);
        }
        return ResponseEntity.ok(MyResponse.ofData(result));
    }
}
