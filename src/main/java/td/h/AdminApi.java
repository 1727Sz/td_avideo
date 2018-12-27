package td.h;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import td.h.o.*;

import java.io.IOException;
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
    @Autowired private FileService fileService;




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
        Pair<Long, List<T_Video>> objects = hAdminRepository.pageVideo(params, page, rows);
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
    public OptionVo addVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, Object> params) throws IOException {
        String title = String.valueOf(params.get("title"));
        String playUrl = String.valueOf(params.get("playUrl"));
        String cover = String.valueOf(params.getOrDefault("cover", ""));
        if (Strings.isNullOrEmpty(cover)) {
            ImagePath path = fileService.save(file);
            cover = path.relativePath();
        }
        if (!hAdminRepository.addVideo(title, cover, playUrl)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/video/edit")
    public OptionVo editVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, Object> params) throws IOException {
        int id = Integer.parseInt(String.valueOf(params.get("id")));
        String title = String.valueOf(params.get("title"));
        String playUrl = String.valueOf(params.get("playUrl"));
        String cover = String.valueOf(params.getOrDefault("cover", ""));
        if (Strings.isNullOrEmpty(cover)) {
            ImagePath path = fileService.save(file);
            cover = path.relativePath();
        }
        if (hAdminRepository.updateVideo(id, title, cover, playUrl)) {
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
        float monthVipPrice = Float.parseFloat(String.valueOf(params.get("monthVipPrice")));
        float quarterVipPrice = Float.parseFloat(String.valueOf(params.get("quarterVipPrice")));
        float yearVipPrice = Float.parseFloat(String.valueOf(params.get("yearVipPrice")));
        if (!hAdminRepository.updateConfiguration(
                ((int) (yearVipPrice * 100)),
                ((int) (quarterVipPrice * 100)),
                ((int) (monthVipPrice * 100)))) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/user/page")
    public PageVo pageUser(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_User>> objects = hAdminRepository.pageUser(params, page, rows);
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
        Pair<Long, List<T_Comment>> objects = hAdminRepository.pageComment(params, page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @GetMapping("/version/page")
    public PageVo pageVersion(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Version>> objects = hAdminRepository.pageVersion(params, page, rows);
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
        Pair<Long, List<T_Refer_User>> objects = hAdminRepository.pageReferUser(params, page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @PostMapping("/referUser/fee")
    public OptionVo operateReferUserFee(@RequestParam Map<String, Object> params) {
        if (!hAdminRepository.operateReferUserFee(params)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/refer/page")
    public PageVo pageRefer(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<ComplexRefer>> objects = hAdminRepository.pageComplexRefer(params, page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }

    @GetMapping("/referFee/page")
    public PageVo pageReferFee(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<T_Refer_Fee.ComplexReferFee>> objects = hAdminRepository.pageReferFee(params, page, rows);
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

    @PostMapping("/referUser/add")
    public OptionVo addReferUser(@RequestParam Map<String, Object> params) {
        if (!hAdminRepository.addReferUser(params)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/referUser/edit")
    public OptionVo editReferUser(@RequestParam Map<String, Object> params) {
        if (!hAdminRepository.updateReferUser(params)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/referUser/delete")
    public OptionVo deleteReferUser(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.deleteReferUser(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/referUser/enable")
    public OptionVo enableReferUser(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.enableReferUser(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @PostMapping("/referUser/disable")
    public OptionVo disableReferUser(@RequestBody Map<String, Object> params) {
        List<Integer> id = Arrays.stream(String.valueOf(params.get("id")).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (!hAdminRepository.disableReferUser(id)) {
            return OptionVo.Fail;
        }
        return OptionVo.OK;
    }

    @GetMapping("/recharge/page")
    public PageVo pageRecharge(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int rows,
            @RequestParam Map<String, Object> params) {
        Pair<Long, List<ComplexVipRecharge>> objects = hAdminRepository.pageVipRecharge(params, page, rows);
        PageVo pageVo = new PageVo(objects.getValue0());
        pageVo.getRows().addAll(objects.getValue1());
        return pageVo;
    }
}
