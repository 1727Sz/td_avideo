package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import td.h.o.DeviceType;
import td.h.o.T_Refer_Fee;
import td.h.o.T_Version;

import java.util.List;
import java.util.Map;

@Mapper
public interface VersionMapper {

    T_Version getLatestVersion(int platform);
    List<T_Version> listVersion(int platform);

    boolean add(Map<String, Object> params);
    long countVersion(Map<String, Object> params);
    List<T_Version> pageVersion(Map<String,Object> params, RowBounds rowBounds);
    boolean delete(List<Integer> id);
    boolean enable(List<Integer> id);
    boolean disable(List<Integer> id);
    boolean update(Map<String, Object> params);
}
