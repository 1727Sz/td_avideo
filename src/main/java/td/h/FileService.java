package td.h;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import td.h.o.BizException;
import td.h.o.ImagePath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @author sanlion do
 */
@Slf4j
@Service
public class FileService {

    @Value("${dir.upload.image}") private String imageUploadDir;
    @Value("${server.domain}") private String domain;

    public ImagePath save(MultipartFile file) throws IOException {
        return save(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
    }

    public ImagePath save(InputStream inputStream, String name, String contentType) {
        ImagePath imagePath = generatePath(name);
//        validateContentType(contentType);
        save(inputStream, imagePath.getLocalTargetDirectory());
        return imagePath;
    }

    private void save(InputStream inputStream, Path target) {
        try {
            Files.copy(inputStream, target);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException(17041701, "file upload failed.");
        }
    }

    private ImagePath generatePath(String name) {
        String directory = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH"));
        name = generateFileName(name);
        Path localTargetDirectory = Paths.get(imageUploadDir, directory);
        URI show = URI.create(directory.concat("/").concat(name));
        try {
            if (!Files.exists(localTargetDirectory)) {
                Files.createDirectories(localTargetDirectory);
            }
            localTargetDirectory = Paths.get(localTargetDirectory.toString(), name);
            return new ImagePath(localTargetDirectory, show);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException(17041701, "file upload failed.");
        }
    }

    private String generateFileName(String name) {
        String[] split = name.split("[.]");
        String extension = split[split.length-1];
        return String.valueOf(Math.abs(Hashing.md5()
                .newHasher()
                .putLong(System.currentTimeMillis())
                .putString(name, Charset.forName("utf8")).hash().asLong()))
                .concat(".")
                .concat(extension);
    }

    private void validateContentType(String contentType) {
        if (!Arrays.stream(SupportedMediaType.values())
                .filter(
                        supportedMediaType ->
                                supportedMediaType.getContentType().equalsIgnoreCase(contentType))
                .findAny().isPresent()) {
            throw new BizException(17041702, "unsupported content type.");
        }
    }

    public enum SupportedMediaType {
        gif("image/gif"),
        jpg("image/jpeg"),
        png("image/png");

        @Getter private String contentType;

        SupportedMediaType(String contentType) {
            this.contentType = contentType;
        }
    }


}
