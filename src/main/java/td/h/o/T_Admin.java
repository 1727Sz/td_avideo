package td.h.o;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class T_Admin {

    private static final String admin_username = "vadmin";
    private static final String admin_password = "vadmin";

    public static final T_Admin defalutAadmin = new T_Admin(admin_username, admin_password);


    int id;
    String username;
    String password;
    String description;
    Date createTime;

    private T_Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
