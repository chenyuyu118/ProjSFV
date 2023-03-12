package pers.cherish.commentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.commentservice.Comment;
import pers.cherish.commentservice.domain.CommentDTO;
import pers.cherish.commentservice.domain.CommentTree;
import pers.cherish.commentservice.domain.CommentTreeNode;
import pers.cherish.commentservice.service.CommentService;
import pers.cherish.response.MyResponse;

import java.util.List;

@RestController
@Tags(value = {
        @io.swagger.v3.oas.annotations.tags.Tag(name = "Comment", description = "评论相关接口")
})
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    // 发布评论
    @PostMapping("/publish/{userId}")
    @PermissionConfirm
    @Operation(summary = "发布评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "发布成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "发布失败")
    })
    public ResponseEntity<MyResponse<Void>> publishComment(@PathVariable Long userId,
                                                           @RequestBody CommentDTO commentDTO) {
        if (commentDTO.getReplyId() == null)
            commentService.publishComment(commentDTO.getVideoId(), commentDTO.getContent(), userId,
                    commentDTO.getParentId());
        else
            commentService.publishComment(commentDTO.getVideoId(), commentDTO.getContent(), commentDTO.getAuthorId(),
                commentDTO.getParentId(), commentDTO.getReplyId());
        return ResponseEntity.ok(MyResponse.ofMessage("发布成功"));
    }

    // 删除评论
    @PostMapping("/delete/{userId}/{videoId}/{commentId}")
    @PermissionConfirm
    @Operation(summary = "删除评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
    })
    public ResponseEntity<MyResponse<Void>> deleteComment(@PathVariable Long userId,
                                                          @PathVariable String videoId,
                                                          @PathVariable Long commentId) {
        commentService.deleteComment(videoId, commentId, userId);
        return ResponseEntity.ok(MyResponse.ofMessage("删除成功"));
    }

    // 获取评论
    @GetMapping("/{videoId}/{page}")
    @Operation(summary = "获取评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<CommentTree>> getCommentTree(@PathVariable String videoId,
                                                                  @PathVariable(required = false) int page) {
        final CommentTree commentTree = commentService.getCommentTree(videoId, page);
        System.out.println(commentTree);
        return ResponseEntity.ok(MyResponse.ofData(commentTree));
    }

    // 获取子评论
    @GetMapping("/{videoId}/{commentId}/{page}")
    @Operation(summary = "获取子评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<List<CommentTreeNode>>> getCommentTree(@PathVariable String videoId,
                                                                            @PathVariable Long commentId,
                                                                            @PathVariable(required = false) int page) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        return ResponseEntity.ok(
                MyResponse.ofData(commentService.getCommentTree(videoId, commentId, page)));
    }

    // 获取评论按时间排序
    @GetMapping("/time/{videoId}/{page}")
    @Operation(summary = "获取评论按时间排序")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<CommentTree>> getCommentTreeByTime(@PathVariable String videoId,
                                                                        @PathVariable(required = false) int page) {
        return ResponseEntity.ok(MyResponse.ofData(commentService.getCommentTreeByTime(videoId, page)));
    }

    // 获取子评论按时间排序
    @GetMapping("/time/{videoId}/{commentId}/{page}")
    @Operation(summary = "获取子评论按时间排序")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<List<CommentTreeNode>>> getCommentTreeByTime(@PathVariable String videoId,
                                                                                  @PathVariable Long commentId,
                                                                                  @PathVariable(required = false) int page) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        return ResponseEntity.ok(
                MyResponse.ofData(commentService.getCommentTreeByTime(videoId, commentId, page)));
    }

    // 获取评论全部树
    @GetMapping("/all/{videoId}")
    @Operation(summary = "获取评论全部树")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<CommentTree>> getAllCommentTree(@PathVariable String videoId) {
        return ResponseEntity.ok(MyResponse.ofData(commentService.getCommentTree(videoId)));
    }

    // 获取自己的评论
    @GetMapping("/my/{userId}/{page}")
    @PermissionConfirm
    @Operation(summary = "获取自己的评论")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<List<Comment>>> getSelfCommentTree(@PathVariable Long userId,
                                                                        @PathVariable(required = false) int page) {
        return ResponseEntity.ok(MyResponse.ofData(commentService.getMyComments(userId, page)));
    }

    // 获取评论的回复
    @GetMapping("/reply/{videoId}/{commentId}")
    @Operation(summary = "获取评论的回复")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "获取失败")
    })
    public ResponseEntity<MyResponse<List<CommentTreeNode>>> getReplyCommentTree(@PathVariable Long commentId,
                                                                         @PathVariable String  videoId,
                                                                         @PathVariable(required = false) int page) {
        if (!commentService.isExist(videoId, commentId)) {
            return ResponseEntity.badRequest().body(MyResponse.ofMessage("评论不存在"));
        }
        return ResponseEntity.ok(MyResponse.ofData(commentService.getCommentReply(videoId, commentId)));
    }
}
