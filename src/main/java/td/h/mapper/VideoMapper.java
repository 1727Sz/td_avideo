package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import td.h.o.T_Video;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

    List<T_Video> page(RowBounds rowBounds);
    List<T_Video> pageFollowed(int uid, RowBounds rowBounds);
    boolean alreadyFollowed(@Param("uid") int uid, @Param("target") int vid);
    boolean follow(@Param("uid") int uid, @Param("target") int vid);
    boolean followCancel(@Param("uid") int uid, @Param("target") int vid);
    T_Video getEnabledVideoById(int vid);

    int count(Map<String, Object> params);
    List<T_Video> adminPage(Map<String, Object> params, RowBounds rowBounds);
    boolean delete(List<Integer> id);
    boolean gte(Map<String,Object> params);
    boolean update(Map<String,Object> params);
}
