package pers.cherish.commentservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.cherish.commentservice.Comment;
import pers.cherish.commentservice.domain.CommentTree;
import pers.cherish.commentservice.domain.CommentTreeNode;
import pers.cherish.commentservice.mapper.CommentMapper;
import pers.cherish.commentservice.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentMapper commentMapper;
    private StringRedisTemplate stringRedisTemplate;

    @Value("${variable.redis.comment-counter-key-prefix}")
    private String commentCounterKeyPrefix;
    @Value("${variable.page.parent-comment-size}")
    private int parentCommentSize;
    @Value("${variable.page.child-comment-size}")
    private int childCommentSize;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setCommentMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    private Long generateCommentId() {
        final LocalDateTime now = LocalDateTime.now();
        final long epochSecond = now.toEpochSecond(ZoneOffset.UTC);

        String date = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        final long counter = stringRedisTemplate.opsForValue().increment(commentCounterKeyPrefix + date);

        return epochSecond << 32 | counter;
    }

    @Override
    public CommentTreeNode publishComment(String videoId, String content, Long userId, Long parentId) {
        final Long commentId = generateCommentId();
        final Comment comment = new Comment(videoId, commentId, userId, content.getBytes(StandardCharsets.UTF_8),
                0L, 0L, LocalDateTime.now(), false, parentId, null);
        commentMapper.insert(comment);
        return CommentTreeNode.builder()
                .commentId(commentId)
                .content(content)
                .commentTime(comment.getCommentTime())
                .likeCount(0L)
                .authorId(userId)
                .child(null)
                .parentId(parentId)
                .replyId(0L)
                .isDeleted(false)
                .build();
    }

    @Override
    public CommentTreeNode publishComment(String videoId, String content, Long userId, Long parentId, Long replyId) {
        final Long commentId = generateCommentId();
        final Comment comment = new Comment(videoId, commentId, userId, content.getBytes(StandardCharsets.UTF_8),
                0L, 0L, LocalDateTime.now(), false, parentId, replyId);
        commentMapper.insert(comment);
        return CommentTreeNode.builder()
                .commentId(commentId)
                .content(content)
                .commentTime(comment.getCommentTime())
                .likeCount(0L)
                .authorId(userId)
                .child(null)
                .parentId(parentId)
                .replyId(replyId)
                .isDeleted(false)
                .build();
    }

    @Override
    public void deleteComment(String videoId, Long commentId, Long authorId) {
        // TODO 鉴定权限
        commentMapper.delete(videoId, commentId);
    }

    @Override
    public CommentTree getCommentTree(String videoId, int k) {
        int startIndex = (k - 1) * parentCommentSize;
        int endIndex = k * parentCommentSize;
        final CommentTree commentTree = new CommentTree();
        commentTree.setVideoId(videoId);
        final List<CommentTreeNode> baseTree = commentMapper.getChildTree(videoId, startIndex, endIndex, 0L);
        // TODO 递归获取子树
        commentTree.setCommentTreeNodes(baseTree);
        return commentTree;
    }

    @Override
    public List<CommentTreeNode> getCommentTree(String videoId, Long commentId, int k) {
        int startIndex = (k - 1) * childCommentSize;
        int endIndex = k * childCommentSize;
        return commentMapper.getChildTree(videoId, startIndex, endIndex, commentId);
    }

    @Override
    public CommentTree getCommentTreeByTime(String videoId, int k) {
        int startIndex = (k - 1) * parentCommentSize;
        int endIndex = k * parentCommentSize;
        final CommentTree commentTree = new CommentTree();
        commentTree.setVideoId(videoId);
        final List<CommentTreeNode> baseTree = commentMapper.getChildTreeByTime(videoId, startIndex, endIndex, 0L);
        // TODO 递归获取子树
        commentTree.setCommentTreeNodes(baseTree);
        return commentTree;
    }

    @Override
    public List<CommentTreeNode> getCommentTreeByTime(String videoId, Long commentId, int k) {
        int startIndex = (k - 1) * childCommentSize;
        int endIndex = k * childCommentSize;
        return commentMapper.getChildTreeByTime(videoId, startIndex, endIndex, commentId);
    }

    @Override
    public CommentTree getCommentTree(String videoId) {
        final CommentTree commentTree = new CommentTree();
        commentTree.setVideoId(videoId);
        final List<CommentTreeNode> treeAll = commentMapper.getChildTreeAll(videoId);
        final Map<Long, CommentTreeNode> collect = treeAll.stream().collect(Collectors.toMap(CommentTreeNode::getCommentId, x->x));
        final List<Long> baseCommentList = collect.keySet().stream().toList();
        final List<CommentTreeNode> childByList = commentMapper.getChildByList(videoId, baseCommentList);
        childByList.forEach(commentTreeNode -> {
            final Long parentId = commentTreeNode.getParentId();
            if (collect.get(parentId).getChild() == null) {
                final ArrayList<CommentTreeNode> treeNodeArrayList = new ArrayList<>();
                treeNodeArrayList.add(commentTreeNode);
                collect.get(parentId).setChild(treeNodeArrayList);
            } else {
                collect.get(parentId).getChild().add(commentTreeNode);
            }
        });
        commentTree.setCommentTreeNodes(collect.values().stream().toList());
        return commentTree;
    }

    @Override
    public List<Comment> getMyComments(Long userId) {
        return commentMapper.getMyComments(userId);
    }

    @Override
    public List<Comment> getMyComments(Long userId, int k) {
        int startIndex = (k - 1) * parentCommentSize;
        int endIndex = k * parentCommentSize;
        return commentMapper.getMyCommentsPages(userId, startIndex, endIndex);
    }

    @Override
    public List<CommentTreeNode> getCommentReply(String videoId, Long commentId) {
        return commentMapper.getCommentReply(videoId, commentId);
    }

    @Override
    public boolean isExist(String videoId, Long commentId) {
        return commentMapper.isExist(videoId, commentId);
    }
}
