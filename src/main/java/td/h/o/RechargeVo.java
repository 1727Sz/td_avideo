package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RechargeVo {

    @ApiModelProperty("充值订单ID，可用于轮询充值状态") String orderNo;
    @ApiModelProperty("第三方充值跳转支付页面") String payUrl;
}
