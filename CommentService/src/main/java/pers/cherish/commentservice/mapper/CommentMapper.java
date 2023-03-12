package pers.cherish.commentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.cherish.commentservice.Comment;
import pers.cherish.commentservice.domain.CommentTreeNode;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    void delete(String videoId, Long commentId);

    List<CommentTreeNode> getChildTree(String videoId, int startIndex, int endIndex, long parentId);

    List<CommentTreeNode> getChildTreeByTime(String videoId, int startIndex, int endIndex, long parentId);

    List<CommentTreeNode> getChildTreeAll(String videoId);

    List<CommentTreeNode> getChildByList(String videoId, List<Long> baseCommentList);

    List<Comment> getMyComments(Long userId);

    List<CommentTreeNode> getCommentReply(String videoId, Long commentId);

    List<Comment> getMyCommentsPages(Long authorId, int startIndex, int endIndex);

    boolean isExist(String videoId, Long commentId);
}
