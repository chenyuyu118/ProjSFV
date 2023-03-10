package pers.cherish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.domain.VideoVo;
import pers.cherish.response.MyResponse;
import pers.cherish.service.VideoLikeService;
import pers.cherish.service.VideoService;

import java.util.List;

@RestController
@RequestMapping("/video/like")
@Tags({
        @Tag(name = "Video", description = "视频点赞相关接口"),
        @Tag(name = "VideoLike", description = "视频点赞相关接口")}
)
public class VideoLikeController {

    private VideoLikeService videoLikeService;
    private VideoService videoService;

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
        return ResponseEntity.ok(MyResponse.ofMessage("取消点踩成功"));
    }
}
