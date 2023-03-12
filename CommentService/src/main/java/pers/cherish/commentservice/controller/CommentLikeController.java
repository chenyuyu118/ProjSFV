package pers.cherish.commentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.commentservice.service.CommentLikeService;
import pers.cherish.commentservice.service.CommentService;
import pers.cherish.response.MyResponse;

@RestController
@Tags(value = {
        @io.swagger.v3.oas.annotations.tags.Tag(name = "CommentLike", description = "评论点赞相关接口")
})
@RequestMapping("/comment")
public class CommentLikeController {
    private CommentLikeService commentLikeService;
    private CommentService commentService;

    @Autowired
    public void setCommentLikeService(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }
    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
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
}
