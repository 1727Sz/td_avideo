package td.h;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import td.h.o.ApiResponse;
import td.h.o.BizException;


/**
 * Created by root on 17-3-6.
 */
@Slf4j
@RestController
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    ApiResponse defaultExceptionHandler(Exception exception) {
        log.error("", exception);
        return new ApiResponse.Fail(false, 17030601, "视频太火爆...");
    }

    @ExceptionHandler(BizException.class)
    ApiResponse bizExceptionHandler(BizException exception) {
        log.info("", exception.getMessage());
        return new ApiResponse.Fail(true, exception.getCode(), exception.getMessage());
    }
}
