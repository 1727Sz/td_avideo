package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class T_Video {

    int id;
    @ApiModelProperty("标题") String title;
    @ApiModelProperty("封面") String cover;
    @ApiModelProperty("播放地址") String playUrl;
    @ApiModelProperty(hidden = true) boolean enable;
    @ApiModelProperty(value = "创建时间", hidden = true) Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true) Date updateTime;
    @ApiModelProperty("点赞") int likes;
    @ApiModelProperty("评论数") int comments;
    @ApiModelProperty("是否会员，非会员无playUrl") boolean vip = true;
    @ApiModelProperty("是否点赞过") boolean liked;
    @ApiModelProperty("是否关注过") boolean followed;

    @ApiModelProperty("时间")
    public String getTime() {
        return Times.format(this.updateTime, Times.yyyyMMddHHmm);
    }

    @Data
    public static class VideoArrayVo {

        private List<T_Video> values = new ArrayList<>();
    }
}
