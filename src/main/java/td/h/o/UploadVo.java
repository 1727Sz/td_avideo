package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadVo {

    @ApiModelProperty("图片地址") String url;
    @ApiModelProperty("图片相对地址") String relativePath;
}
