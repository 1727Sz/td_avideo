package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {

    boolean alreadyLiked(@Param("target") int target, @Param("uid") int uid);
    boolean like(@Param("target") int target, @Param("uid") int uid);
    boolean syncLikes(int vid);
}
