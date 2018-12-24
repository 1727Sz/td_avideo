package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class UserRegisterRb {

    String username;
    String password;
    @ApiModelProperty("分享码，非必填") int refer;
}
