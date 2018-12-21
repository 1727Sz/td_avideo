package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class T_Configuration {

    @ApiModelProperty("会员价格，月") int monthVipPrice;
    @ApiModelProperty("会员价格，季度") int quarterVipPrice;
    @ApiModelProperty("会员价格，年") int yearVipPrice;
}
