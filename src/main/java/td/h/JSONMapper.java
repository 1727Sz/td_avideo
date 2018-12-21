package td.h;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

/**
 * @author sanlion do
 */
public class JSONMapper {

    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String src, Class<T> classOfT) {
        if (Strings.isNullOrEmpty(src)) {
            return null;
        }
        return gson.fromJson(src, classOfT);
    }

    public static <T> T fromJson(String src, Class<T> classOfT, T ifNull) {
        T target = fromJson(src, classOfT);
        if (Objects.isNull(target)) {
            return ifNull;
        }
        return target;
    }

}
