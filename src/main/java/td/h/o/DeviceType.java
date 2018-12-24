package td.h.o;

import lombok.Getter;

/**
 * @author sanlion do
 */
public enum DeviceType {

    Android(1), iOS(2), H5(3);
    @Getter private int code;

    DeviceType(int code) {
        this.code = code;
    }
}
