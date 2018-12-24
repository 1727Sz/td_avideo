package td.h;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import td.h.o.ApiResponse;
import td.h.o.DeviceType;
import td.h.o.T_Version;

import static td.h.o.ApiHeader.HK_DEVICE_TYPE;
import static td.h.o.ApiHeader.HK_VERSION;

/**
 * @author sanlion do
 */
@RestController
@RequestMapping("/system/version")
@Api(tags = "system-version", description = "系统-app版本")
public class AppVersionApi {

    @Autowired private AppVersionService appVersionService;

    @ApiOperation(
            value = "get latest version information, if should be upgraded.",
            response = T_Version.AppVersionVo.class)
    @GetMapping
    public ApiResponse latest(
            @RequestHeader(HK_DEVICE_TYPE) DeviceType deviceType,
            @RequestHeader(HK_VERSION) String version) {
        return new ApiResponse.Ok(
                "",
                new T_Version.AppVersionVo(
                        appVersionService.latestVersion(deviceType, version)));

//        return new ApiResponse.Fail(true, 17041211, "已是最新版本");
    }

    @ApiOperation(
            value = "get latest version information",
            response = T_Version.AppVersionVo.class)
    @GetMapping("/latest")
    ApiResponse getLatestVersion(
            @RequestHeader(required = false, value = HK_DEVICE_TYPE, defaultValue = "Android") DeviceType deviceType,
            @RequestHeader(required = false) String bundleid) {
        return new ApiResponse.Ok(
                "",
                new T_Version.AppVersionVo(appVersionService.latestVersion(deviceType), bundleid));
    }
}
