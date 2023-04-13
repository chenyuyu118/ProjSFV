package pers.cherish.commentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.commentservice.service.CommentLikeService;
import pers.cherish.commentservice.service.CommentService;
import pers.cherish.response.MyResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Tags(value = {
        @io.swagger.v3.oas.annotations.tags.Tag(name = "CommentLike", description = "评论点赞相关接口")
})
@RequestMapping("/comment")
public class CommentLikeController {
    private CommentLikeService commentLikeService;
    private CommentService commentService;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${variable.redis.comment-like-key-prefix}")
    private String commentLikeKeyPrefix;
    @Value("${variable.redis.comment-dislike-key-prefix}")
    private String commentDislikePrefix;

    @Autowired
    public void setCommentLikeService(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }
    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 点赞评论
    @PostMapping("/like/{userId}/{videoId}/{commentId}")
    @Operation(summary = "点赞评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "点赞失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Void>> likeComment(@PathVariable Long userId,
                                                  @PathVariable String videoId,
                                                  @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        String key = commentLikeKeyPrefix + userId + ":" + videoId;
        stringRedisTemplate.opsForSet().add(key, commentId.toString());
        stringRedisTemplate.opsForSet();
        commentLikeService.likeComment(videoId, commentId, userId);
        return ResponseEntity.ok(MyResponse.ofMessage("点赞成功"));
    }

    // 取消点赞评论
    @DeleteMapping("/like/{userId}/{videoId}/{commentId}")
    @Operation(summary = "取消点赞评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消点赞成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "取消点赞失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Void>> cancelLikeComment(@PathVariable Long userId,
                                  @PathVariable String videoId,
                                  @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        commentLikeService.cancelLikeComment(videoId, commentId, userId);
        String key = commentLikeKeyPrefix + userId + ":" + videoId;
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.opsForSet().remove(key, commentId.toString());
        }
        return ResponseEntity.ok(MyResponse.ofMessage("取消点赞成功"));
    }

    // 点踩评论
    @PostMapping("/dislike/{userId}/{videoId}/{commentId}")
    @Operation(summary = "点踩评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "点踩成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "点踩失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Void>> dislikeComment(@PathVariable Long userId,
                               @PathVariable String videoId,
                               @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        commentLikeService.dislikeComment(videoId, commentId, userId);
        String key = commentDislikePrefix + userId + ":" + videoId;
        stringRedisTemplate.opsForSet().add(key, commentId.toString());
        return ResponseEntity.ok(MyResponse.ofMessage("点踩成功"));
    }

    // 取消点踩评论
    @DeleteMapping("/dislike/{userId}/{videoId}/{commentId}")
    @Operation(summary = "取消点踩评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消点踩成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "取消点踩失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Void>> cancelDislikeComment(@PathVariable Long userId,
                                     @PathVariable String videoId,
                                     @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        commentLikeService.cancelDislikeComment(videoId, commentId, userId);
        String key = commentDislikePrefix + userId + ":" + videoId;
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.opsForSet().remove(key, commentId.toString());
        }
        return ResponseEntity.ok(MyResponse.ofMessage("取消点踩成功"));
    }

    // 获取是否点赞评论
    @GetMapping("/like/{userId}/{videoId}/{commentId}")
    @Operation(summary = "获取是否点赞评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Boolean>> isLikeComment(@PathVariable Long userId,
                                 @PathVariable String videoId,
                                 @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        return ResponseEntity.ok(MyResponse.ofData(commentLikeService.isLikeComment(videoId, commentId, userId)));
    }

    // 获取是否点踩评论
    @GetMapping("/dislike/{userId}/{videoId}/{commentId}")
    @Operation(summary = "获取是否点踩评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<Boolean>> isDislikeComment(@PathVariable Long userId,
                                    @PathVariable String videoId,
                                    @PathVariable Long commentId) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        return ResponseEntity.ok(MyResponse.ofData(commentLikeService.isDislikeComment(videoId, commentId, userId)));
    }

    private RedisScript<List> getCommentIsLikeScript;

    @Autowired
    @Qualifier("getCommentIsLike")
    public void setGetCommentIsLikeScript(RedisScript<List> getCommentIsLikeScript) {
        this.getCommentIsLikeScript = getCommentIsLikeScript;
    }

    // 批量获取是否点赞评论
    @GetMapping("/like/test/{userId}/{videoId}")
    public ResponseEntity<MyResponse<List<Boolean>>> isLikeRangeComment(@PathVariable Long userId,
                                                                  @PathVariable String videoId,
                                                                  @RequestParam(name = "ids")ArrayList<Long> ids) {
        String key = commentLikeKeyPrefix + userId + ":" + videoId;
        if (!stringRedisTemplate.hasKey(key)) {
            List<Boolean> list = Stream.generate(() -> Boolean.FALSE).limit(ids.size()).toList();
            return ResponseEntity.ok(MyResponse.ofData(list));
        } else {
            List<String> list = stringRedisTemplate.execute(
                    getCommentIsLikeScript,
                    List.of(key, key + ":temp"),
                    ids.stream().map(String::valueOf).toArray());
            ArrayList<Boolean> result = new ArrayList<>();
            System.out.println(list);
            for (int i = 0; i < ids.size(); i++) {
                if (list.contains(ids.get(i).toString())) {
                    result.add(i, true);
                } else {
                    result.add(i, false);
                }
            }
            return ResponseEntity.ok(MyResponse.ofData(result));
        }
    }

    // 批量获取是否点踩评论
    @GetMapping("/dislike/test/{userId}/{videoId}")
    public ResponseEntity<MyResponse<List<Boolean>>> isDislikeRangeComment(@PathVariable Long userId,
                                                                        @PathVariable String videoId,
                                                                        @RequestParam(name = "ids")ArrayList<Long> ids) {
        String key = commentDislikePrefix + userId + ":" + videoId;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            List<Boolean> list = Stream.generate(() -> Boolean.FALSE).limit(ids.size()).toList();
            return ResponseEntity.ok(MyResponse.ofData(list));
        } else {
            List list = stringRedisTemplate.execute(getCommentIsLikeScript, List.of(key, key + ":temp"),
                    ids.stream().map(String::valueOf).toArray());
            System.out.println(list);
            ArrayList<Boolean> result = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                result.add(i, list.contains(ids.get(i)));
            }
            return ResponseEntity.ok(MyResponse.ofData(result));
        }
    }
}
