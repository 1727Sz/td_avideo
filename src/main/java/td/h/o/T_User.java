package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;
import java.util.Objects;

@Data
public class T_User {

    int id;
    @ApiModelProperty("昵称") String nickname;
    @ApiModelProperty("头像") String avatar;
    @ApiModelProperty(hidden = true) String relativeAvatar;
    @ApiModelProperty(hidden = true) String username;
    @ApiModelProperty(hidden = true) String password;
    @ApiModelProperty("登陆令牌") String token;
    @ApiModelProperty(hidden = true) Date tokenExpireTime;
    @ApiModelProperty(hidden = true) Date createTime;
    @ApiModelProperty(hidden = true) Date vipExpireDate;
    @ApiModelProperty(hidden = true) int role;
    @ApiModelProperty(hidden = true) int registerRefer;

    public static String showNickname(String nickname, String username) {
        if (!Strings.isEmpty(nickname)) {
            return nickname;
        }
        return username.substring(0, username.length() - 2) + "**";
    }

    @ApiModelProperty("是否会员")
    public boolean isVip() {
        return Objects.nonNull(this.vipExpireDate) && this.vipExpireDate.after(new Date());
    }

    @ApiModelProperty("会员有效期")
    public String getVipExpireDay() {
        return Times.format(this.vipExpireDate, "yyyy-MM-dd");
    }
}
