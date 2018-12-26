package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.h.Moneys;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class T_Configuration {

    @ApiModelProperty("会员价格，月") int monthVipPrice;
    @ApiModelProperty("会员价格，季度") int quarterVipPrice;
    @ApiModelProperty("会员价格，年") int yearVipPrice;

    public String getPrettyMonthVipPrice(){
        return Moneys.format(this.monthVipPrice);
    }
    public String getPrettyQuarterVipPrice(){
        return Moneys.format(this.quarterVipPrice);
    }

    public String getPrettyYearVipPrice(){
        return Moneys.format(this.yearVipPrice);
    }

}
