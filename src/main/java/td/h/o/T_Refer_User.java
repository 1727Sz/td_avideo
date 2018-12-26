package td.h.o;

import lombok.Data;
import td.h.Moneys;

import java.util.Date;

@Data
public class T_Refer_User {

    int id;
    String name;
    String description;
    int referCount;
    float rate;
    int balance;
    Date createTime;
    boolean enable;
    String url;

    public String getPrettyBalance(){
        return Moneys.format(this.balance);
    }
}
