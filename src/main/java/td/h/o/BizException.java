package td.h.o;

import lombok.Getter;

import java.text.MessageFormat;

/**
 * Created by root on 17-3-6.
 */
public class BizException extends RuntimeException {

    @Getter
    private int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, String... params) {
        super(MessageFormat.format(message, params));
        this.code = code;
    }

    public BizException(int code) {
        this.code = code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
