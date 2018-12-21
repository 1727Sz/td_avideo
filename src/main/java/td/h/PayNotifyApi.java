package td.h;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("notify")
public class PayNotifyApi {

    @PostMapping("async")
    public String testNotify(@RequestParam Map<String, String> params) {

        log.info("cp 收到异步回调---》》》 " + params);
        return "success";

    }
    @PostMapping("sync")
    public String testCallback(@RequestParam Map<String, String> params) {
        log.info(" 收到同步回调 " + params);
        return "success";
    }
}
