package td.h;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import td.h.o.BizException;
import td.h.o.DeviceType;
import td.h.o.T_Version;

import java.util.List;
import java.util.Objects;

/**
 * @author sanlion do
 */
@Service
public class AppVersionService {

    public static final BizException alreadyLatestVersionBizException = new BizException(17041211, "已是最新版本");

    @Autowired private HRepository hRepository;

    /**
     * get latest version, if should be upgrade
     *
     * @param deviceType
     * @param appVersion
     * @return
     */
    public T_Version latestVersion(DeviceType deviceType, String appVersion) {

        T_Version systemVersion = getLatestVersion(deviceType);
        if (Objects.isNull(systemVersion)) {
            throw alreadyLatestVersionBizException;
        }

        int appVersionNo = Integer.parseInt(appVersion.replaceAll("[.]", ""));
        if (appVersionNo >= systemVersion.getNumericVersion()) {
            throw alreadyLatestVersionBizException;
        }
        systemVersion.setUpgrade(systemVersion.getNumericLowVersion() > appVersionNo);
        return systemVersion;
    }

    public T_Version latestVersion(DeviceType deviceType) {
        T_Version latestVersion = hRepository.getLatestVersion(deviceType);
        if (Objects.isNull(latestVersion)) {
            throw alreadyLatestVersionBizException;
        }
        return latestVersion;
    }

    private T_Version getLatestVersion(DeviceType deviceType) {
        List<T_Version> value = hRepository.listVersion(deviceType);
        if (Objects.isNull(value) || value.isEmpty()) {
            return null;
        }
        return value.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getNumericVersion(), o1.getNumericVersion()))
                .findFirst().get();
    }
}
