package td.h.o;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by root on 17-3-5.
 */
@Data
public class ApiResponse {

    private static final NullObj Null = new NullObj();

    @ApiModelProperty("ok?") private boolean ok = true;
    @ApiModelProperty("response code") private int code = 0;
    @ApiModelProperty("response msg") private String msg;
    @ApiModelProperty("object data") private Object data = Null;

    @Data
    public static  class NullObj implements Serializable {
        private String __ = "please ignore the business data.";
    }

    /**
     * Created by root on 17-3-5.
     */
    @Data
    public static class Fail extends ApiResponse {

        public Fail(boolean ok, int code, String message) {
            super();
            this.setOk(ok);
            this.setCode(code);
            this.setMsg(message);
        }
    }

    /**
     * Created by root on 17-3-5.
     */
    @Data
    @NoArgsConstructor
    public static class Ok extends ApiResponse {

        public Ok(String message) {
            super();
            this.setMsg(message);
        }

        public Ok(String message, Object obj) {
            this(message);
            this.setData(obj);
        }
    }
}
