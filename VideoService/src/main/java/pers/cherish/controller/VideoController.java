package pers.cherish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.domain.VideoDTO;
import pers.cherish.domain.VideoDTOUpdate;
import pers.cherish.response.MyResponse;
import pers.cherish.service.VideoService;
import pers.cherish.videoservice.model.Video;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/video")
@Tags({@Tag(name = "Video", description = "视频相关接口")})
public class VideoController {
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

    // 上传视频
    @PostMapping("/upload")
    @Operation(summary = "上传视频", description = "上传视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "上传成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "上传失败")
            }
    )
    public ResponseEntity<MyResponse<String>> uploadVideo(@RequestBody VideoDTO videoDTO,
                                                          @Parameter(hidden = true) HttpServletRequest request) {
        final String token = request.getHeader("token");
        final Long s = Long.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("token:" + token)));
        if (!Objects.equals(s, videoDTO.getAuthorId()))
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("没有操作权限"));
        return ResponseEntity.ok(MyResponse.ofData(videoService.uploadVideo(videoDTO)));
    }

    // 删除视频
    @PostMapping("/delete/{videoId}")
    @Operation(summary = "删除视频", description = "删除视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
            }
    )
    @Parameter(name = "videoId", description = "视频id", required = true, in = ParameterIn.PATH, example = "1")
    public ResponseEntity<MyResponse<Void>> deleteVideo(@PathVariable String videoId,
                                                        @Parameter(hidden = true) HttpServletRequest request) {
        if (!verifyPermission(videoId, request))
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("没有操作权限"));
        videoService.deleteVideo(videoId);
        return ResponseEntity.ok(MyResponse.ofMessage("删除成功"));
    }

    // 修改视频信息
    @PutMapping("/update/info")
    @Operation(summary = "修改视频信息", description = "修改视频信息")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "修改失败")
            }
    )
    public ResponseEntity<MyResponse<Void>> updateVideoInfo(@RequestBody VideoDTOUpdate videoDTO,
                                                            @Parameter(hidden = true) HttpServletRequest request) {
        final String token = request.getHeader("token");
        final Long s = Long.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("token:" + token)));
        final Long authorId = videoDTO.getAuthorId();
        if (!Objects.equals(s, authorId))
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("没有操作权限"));
        videoService.updateVideoInfo(videoDTO);
        return ResponseEntity.ok(MyResponse.ofMessage("修改成功"));
    }

    // 修改视频内容
    @PutMapping("/update/content/{videoId}")
    @Operation(summary = "修改视频内容", description = "修改视频内容")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "修改失败")
            }
    )
    public ResponseEntity<MyResponse<String >> updateVideoContent(@PathVariable String videoId,
                                                                  @Parameter(hidden = true) HttpServletRequest request) {
        if (!verifyPermission(videoId, request))
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("没有操作权限"));
        return ResponseEntity.ok(MyResponse.ofData(videoService.updateVideoContent(videoId)));
    }

    private boolean verifyPermission(@PathVariable String videoId, HttpServletRequest request) {
        final String token = request.getHeader("token");
        final Long s = Long.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("token:" + token)));
        final Video video = videoService.getVideoById(videoId);
        if (video == null)
            return false;
        final Long authorId = video.getAuthorId();
        return Objects.equals(s, authorId);
    }

    // 获取视频信息
    @Operation(summary = "获取视频信息", description = "获取视频信息")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
            }
    )
    @GetMapping("/{videoId}")
    public ResponseEntity<MyResponse<Video>> getVideoInfo(@PathVariable String videoId) {
        final Video video = videoService.getVideoById(videoId);
        if (video == null) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("视频不存在"));
        }
        return ResponseEntity.ok(MyResponse.ofData(video));
    }

    // 搜索视频
    @GetMapping("/search/{key}")
    @Operation(summary = "搜索视频", description = "搜索视频")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "搜索成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "搜索失败")
            }
    )
    public ResponseEntity<MyResponse<List<Video>>> searchVideo(@PathVariable String key) {
        return ResponseEntity.ok(MyResponse.ofData(videoService.searchVideo(key)));
    }

    /**
     * 随机获取一个视频列表
     */
    @GetMapping("/random")
    @Operation(summary = "随机获取一个视频列表", description = "随机获取一个视频列表")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
            }
    )
    public ResponseEntity<MyResponse<List<Video>>> getRandomVideo() {
        return ResponseEntity.ok(MyResponse.ofData(videoService.getRandomVideo()));
    }
}
