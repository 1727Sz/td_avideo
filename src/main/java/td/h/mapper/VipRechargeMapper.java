package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import td.h.o.ComplexVipRecharge;
import td.h.o.T_Version;
import td.h.o.T_VipRecharge;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VipRechargeMapper {

    boolean gte(Map<String, Object> params);
    T_VipRecharge getByOrderNo(String orderNo);
    boolean updateRechargeState(@Param("orderNo") String orderNo, @Param("payState") int payState, @Param("payTime")Date payTime);
    boolean updateVipExpire(@Param("id") int uid, @Param("vipExpireTime") Date vipExpireTime);


    long count(Map<String, Object> params);
    List<ComplexVipRecharge> adminPage(Map<String,Object> params, RowBounds rowBounds);

    boolean expire();
}
