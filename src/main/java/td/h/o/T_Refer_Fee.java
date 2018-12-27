package td.h.o;

import lombok.Data;
import td.h.Moneys;

import java.util.Date;

@Data
public class T_Refer_Fee {

    int id;
    int ruid;
    int sourceType; // // 1.下线充值抽成
    String source;  // 具体的来源ID， 如会员充值订单ID
    int sourceValue;
    int value; // 资金变动金额（有+/-之分，前者为入账，后者为出账）
    Date createTime;
    String description;
    float rate;

    public String getPrettySourceValue() {
        return Moneys.format(this.sourceValue);
    }

    public String getPrettyValue() {
        return Moneys.format(this.value);
    }

    @Data
    public static class ComplexReferFee extends T_Refer_Fee {

        String name;
    }
}
