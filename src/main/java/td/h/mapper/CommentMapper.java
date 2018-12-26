package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import td.h.o.T_Comment;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    boolean comment(Map<String, Object> params);
    boolean syncVideoComments(int vid);
    T_Comment getCommentById(int cid);
    boolean deleteComment(@Param("id") int cid, @Param("uid") int uid);
    List<T_Comment> page(@Param("target") int target, RowBounds rowBounds);

    boolean delete(List<Integer> id);
    long countComment(Map<String, Object> params);
    List<T_Comment> adminPage(Map<String, Object> params, RowBounds rowBounds);
}
