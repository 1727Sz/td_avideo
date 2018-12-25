package td.h;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import td.h.o.*;

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

    @GetMapping("/version/page")
    public PageVo pageVersion(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Version>> objects = hAdminRepository.pageVersion(page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @PostMapping("/version/delete")
    public OptionVo deleteVersion(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.deleteVersion(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/version/enable")
    public OptionVo enableVersion(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.enableVersion(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/version/disable")
    public OptionVo disableVersion(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.disableVersion(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/version/edit")
    public OptionVo editVersion(@RequestParam Map<String, Object> params) {
        int id = Integer.parseInt(String.valueOf(params.get("id")));
        DeviceType platform = DeviceType.ofCode(Integer.parseInt(String.valueOf(params.get("platform"))));
        String version = String.valueOf(params.get("version"));
        String lowVersion = String.valueOf(params.get("low_version"));
        String apkUrl = String.valueOf(params.get("apk"));
        String downloadUrl = String.valueOf(params.get("download"));
        String apkSize = String.valueOf(params.get("apkSize"));
        String remark = String.valueOf(params.get("remark"));
        boolean upgrade = Boolean.parseBoolean(String.valueOf(params.get("upgrade")));


        if (!hAdminRepository.updateVersion(id, platform, version, lowVersion, apkUrl, downloadUrl, apkSize, remark, upgrade)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/version/add")
    public OptionVo addVersion(@RequestParam Map<String, Object> params) {
        DeviceType platform = DeviceType.ofCode(Integer.parseInt(String.valueOf(params.get("platform"))));
        String version = String.valueOf(params.get("version"));
        String lowVersion = String.valueOf(params.get("low_version"));
        String apkUrl = String.valueOf(params.get("apk"));
        String downloadUrl = String.valueOf(params.get("download"));
        String apkSize = String.valueOf(params.get("apkSize"));
        String remark = String.valueOf(params.get("remark"));
        boolean upgrade = Boolean.parseBoolean(String.valueOf(params.get("upgrade")));


        if (!hAdminRepository.addVersion(platform, version, lowVersion, apkUrl, downloadUrl, apkSize, remark, upgrade)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/referUser/page")
    public PageVo pageReferUser(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Refer_User>> objects = hAdminRepository.pageReferUser(page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @GetMapping("/refer/page")
    public PageVo pageRefer(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        int ruid = getRuid(params);
        Pair<Long, List<ComplexRefer>> objects = hAdminRepository.pageRefer(page, rows, ruid);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @GetMapping("/referFee/page")
    public PageVo pageReferFee(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        int ruid = getRuid(params);
        Pair<Long, List<T_Refer_Fee.ComplexReferFee>> objects = hAdminRepository.pageReferFee(page, rows, ruid);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    private int getRuid(Map<String, Object> params) {
        if (!params.containsKey("ruid")) {
            return 0;
        }
        String value = String.valueOf(params.get("ruid"));
        if (Strings.isNullOrEmpty(value)) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}
