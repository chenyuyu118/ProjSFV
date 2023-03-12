package pers.cherish.commentservice.domain;

import lombok.Data;

import java.util.List;

@Data
public class CommentTree {
    // 评论树id
    private String videoId;
    // 评论树内容
    List<CommentTreeNode> commentTreeNodes;
}
