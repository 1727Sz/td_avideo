package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import td.h.o.T_Admin;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    long count(Map<String, Object> params);
    List<T_Admin> page(Map<String, Object> params, RowBounds rowBounds);
    boolean createNewAdmin(Map<String, Object> params);
    boolean deleteAdmin(List<Integer> id);
    boolean updatePassword(Map<String, Object> params);
    T_Admin getByUserName(String username);
    boolean  alreadyHasUsername(String username);
}
