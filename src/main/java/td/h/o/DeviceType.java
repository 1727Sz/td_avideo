package td.h.o;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author sanlion do
 */
public enum DeviceType {

    Android(1), iOS(2), H5(3);
    @Getter private int code;

    DeviceType(int code) {
        this.code = code;
    }

    public static DeviceType ofCode(int code){
        return Arrays.stream(values()).filter(it -> it.getCode() == code).findAny().orElse(Android);
    }
}
