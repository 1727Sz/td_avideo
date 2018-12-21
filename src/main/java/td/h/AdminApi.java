package td.h;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import td.h.o.T_Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApi {

    @Autowired private HAdminRepository hAdminRepository;


    @Data
    @RequiredArgsConstructor
    public static class PageVo {

        List<Object> rows = new ArrayList<>();
        @NonNull long total;

    }

    @Data
    @AllArgsConstructor
    public static class OptionVo {

        boolean ok;
        String msg;
    }

    @GetMapping("/video/page")
    public PageVo pageVideo(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Video>> objects = hAdminRepository.pageVideo(page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }
}
