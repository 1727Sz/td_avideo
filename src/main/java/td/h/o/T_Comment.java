package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class T_Comment {

    int id;
    @ApiModelProperty("内容") String content;
    @ApiModelProperty(hidden = true) Date createTime;
    @ApiModelProperty(value = "视频ID", hidden = true) int vid;
    @ApiModelProperty(value = "用户ID", hidden = true) int uid;
    @ApiModelProperty("用户昵称") String nickname;
    @ApiModelProperty("用户头像") String avatar;
    boolean zj;

    @Data
    public static class CommentArrayVo {

        List<T_Comment> values = new ArrayList<>();
    }

    @ApiModelProperty("时间")
    public String getTime() {
        return Times.format(this.createTime, Times.yyyyMMddHHmm);
    }
}
