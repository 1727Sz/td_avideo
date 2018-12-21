package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRb {

    @ApiModelProperty("当前登陆密码") String password;
    @ApiModelProperty("新密码") String newPassword;
}
