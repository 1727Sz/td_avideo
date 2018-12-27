package td.h;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Index {

    @RequestMapping("admin")
    public String index(){
        return "forward: admin/index.html";
    }
}
