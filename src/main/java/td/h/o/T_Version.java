package td.h.o;

import com.google.common.base.Strings;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class T_Version {

    int platform;
    String versionNo;
    String lowVersionNo;
    String apkUrl;
    String downloadUrl;
    String apkSize;
    String remark;
    boolean upgrade;
    int numericVersion;

    public int getNumericVersion() {
        return Integer.parseInt(this.versionNo.replaceAll("[.]", ""));
    }

    public int getNumericLowVersion() {
        return Integer.parseInt(this.lowVersionNo.replaceAll("[.]", ""));
    }

    @Data
    public static class AppVersionVo {

        @ApiModelProperty("版本号") private String version;
        @ApiModelProperty("版本描述") private String remark;
        @ApiModelProperty("包大小。形如512M") private String size;
        @ApiModelProperty("是否强制升级版本") private boolean compel;
        @ApiModelProperty("包下载地址") private String downloadUrl;
        @ApiModelProperty("下载市场跳转地址") private String marketUrl;

        public AppVersionVo(T_Version version) {
            this.version = version.getVersionNo();
            this.remark = version.getRemark();
            this.size = version.getApkSize();
            this.compel = version.isUpgrade();
            this.downloadUrl = version.getDownloadUrl();
            this.marketUrl = version.getDownloadUrl();
        }

        public AppVersionVo(T_Version version, String bundleid) {
            this(version);
            if (!Strings.isNullOrEmpty(bundleid)) {
                this.downloadUrl = generateApkUrl(this.downloadUrl, bundleid);
            }
        }
    }

    public static String generateApkUrl(String originApkUrl, String bundleid) {

        try {
            if (Strings.isNullOrEmpty(originApkUrl) || !originApkUrl.contains("apk")) {
                return originApkUrl;
            }
            String[] ss = originApkUrl.split("/");
            String name = ss[ss.length - 1];
            String[] split = name.split("[.]");
            String newName = split[0] + "_" + bundleid + "." + split[1];
            String newApkUrl = originApkUrl.replace(name, newName);
            return newApkUrl;
        } catch (Exception e) {
            return originApkUrl;
        }

    }
}
