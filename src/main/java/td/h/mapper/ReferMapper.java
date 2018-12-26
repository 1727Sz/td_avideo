package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import td.h.o.ComplexRefer;
import td.h.o.T_Refer_Fee;
import td.h.o.T_Refer_User;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReferMapper {

    boolean addReferUser(Map<String, Object> params);
    boolean updateReferUser(Map<String, Object> params);
    boolean deleteReferUser(List<Integer> ids);
    boolean enableReferUser(List<Integer> ids);
    boolean disableReferUser(List<Integer> ids);
    long countReferUser(Map<String,Object> params);
    List<T_Refer_User> pageReferUser(Map<String,Object> params, RowBounds rowBounds);

    long countComplexRefer(Map<String, Object> params);
    List<ComplexRefer> pageComplexRefer(Map<String,Object> params, RowBounds rowBounds);
    boolean gteDefaultRefer(Map<String, Object> params);

    long countComplexReferFee(Map<String, Object> params);
    List<T_Refer_Fee.ComplexReferFee> pageComplexReferFee(Map<String,Object> params, RowBounds rowBounds);
}
