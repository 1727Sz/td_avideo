package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import td.h.Moneys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RechargePreviewVo {

    @ApiModelProperty("年卡") VipFee year;
    @ApiModelProperty("季度卡") VipFee quarter;
    @ApiModelProperty("月卡") VipFee month;

    public RechargePreviewVo(T_Configuration configuration, LocalDateTime currentVipExpire) {
        this.year = new VipFee(currentVipExpire, 360, configuration.getYearVipPrice());
        this.quarter = new VipFee(currentVipExpire, 120, configuration.getQuarterVipPrice());
        this.month = new VipFee(currentVipExpire, 30, configuration.getMonthVipPrice());
    }

    @Data
    public static class VipFee {

        @ApiModelProperty("价格，已格式化") String price;
        @ApiModelProperty("预计到期时间") String expireTime;
        @ApiModelProperty("天") int day;

        public VipFee(LocalDateTime currentVipExpire, int day, int price) {

            this.price = Moneys.format(price);
            this.day = day;
            this.expireTime = currentVipExpire.plusDays(day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
