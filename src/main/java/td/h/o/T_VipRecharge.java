package td.h.o;

import lombok.Data;
import td.h.Moneys;

import java.util.Date;

@Data
public class T_VipRecharge {

    String orderNo;
    int uid;
    int fee;
    Date createTime;
    String goodsName;
    int vipPlusDay;
    int payState;
    Date payExpireTime;
    Date payTime;
    String payUrl;

    public String getPrettyFee(){
        return Moneys.format(this.fee);
    }
}
