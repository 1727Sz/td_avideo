package td.h.o;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author sanlion do
 */

@AllArgsConstructor
public class ImagePath {

    @Getter private Path localTargetDirectory;
    @Getter private URI relativePath;

    public String showPath(String domain) {

        return URI.create(domain.concat(relativePath.toString())).toString();
    }

    public String relativePath() {
        return relativePath.toString();
    }

    /**
     * 工具方法，生成完整的图片地址
     *
     * @param domain
     * @param relativePath
     * @return
     */
    public static String showAvatar(String domain, String relativePath) {
        if (Strings.isNullOrEmpty(relativePath)) {
            return null;
        }
        return URI.create(domain.concat(relativePath)).toString();
    }
}
