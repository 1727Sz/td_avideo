package td.h;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import td.h.o.ApiResponse;
import td.h.o.T_Comment;
import td.h.o.T_User;
import td.h.o.T_Video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminApi {

    @Autowired private HRepository hRepository;
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

        public static final OptionVo OK = new OptionVo(true, "操作成功");
        public static final OptionVo Fail = new OptionVo(true, "操作失败");

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

    @PostMapping("/video/delete")
    public OptionVo deleteVideo(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.deleteVideo(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/video/add")
    public OptionVo addVideo(@RequestParam Map<String, Object> params) {
        String title = String.valueOf(params.get("title"));
        String cover = String.valueOf(params.get("cover"));
        String playUrl = String.valueOf(params.get("playUrl"));
        if (!hAdminRepository.addVideo(title, cover, playUrl)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/video/edit")
    public OptionVo editVideo(@RequestParam Map<String, Object> params) {
        int id = Integer.parseInt(String.valueOf(params.get("id")));
        String title = String.valueOf(params.get("title"));
        String cover = String.valueOf(params.get("cover"));
        String playUrl = String.valueOf(params.get("playUrl"));
        if (!hAdminRepository.updateVideo(id, title, cover, playUrl)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/configuration/page")
    public PageVo pageConfiguration() {
        PageVo pageVo = new PageVo(1);
        pageVo.getRows().add(hRepository.getConfiguration());
        return pageVo;
    }

    @PostMapping("/configuration/edit")
    public OptionVo editConfiguration(@RequestParam Map<String, Object> params) {
        int monthVipPrice = Integer.parseInt(String.valueOf(params.get("monthVipPrice")));
        int quarterVipPrice = Integer.parseInt(String.valueOf(params.get("quarterVipPrice")));
        int yearVipPrice = Integer.parseInt(String.valueOf(params.get("yearVipPrice")));
        if (!hAdminRepository.updateConfiguration(yearVipPrice, quarterVipPrice, monthVipPrice)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/user/page")
    public PageVo pageUser(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_User>> objects = hAdminRepository.pageUser(page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @PostMapping("/comment/delete")
    public OptionVo deleteComment(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.deleteComment(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/comment/page")
    public PageVo pageComment(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Comment>> objects = hAdminRepository.pageComment(page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }
}
