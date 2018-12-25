package td.h.o;

import lombok.Data;

import java.util.Date;

@Data
public class T_Refer {

    int id;
    int ruid;
    int channel;
    Date createTime;
    int referCount;
}
