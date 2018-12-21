package td.h.o;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Created by PC on 2017/3/23.
 */
public class Times {
    public static final String __ = "M月dd日 HH:mm";
    public static final String MMddHHmm = "MM-dd HH:mm";
    public static final String MMddHHmmss = "MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化 <- 默认值，空
     *
     * @param date    时间
     * @param pattern 模式
     * @return
     */
    public static String format(Date date, String pattern) {
        return format(date, pattern, "");
    }

    /**
     * 格式化
     *
     * @param date    时间
     * @param pattern 模式
     * @param ifNull  默认值
     * @return
     */
    public static String format(Date date, String pattern, String ifNull) {
        if (Objects.isNull(date)) {
            return ifNull;
        }
        try {
            return
                    LocalDateTime
                            .ofInstant(date.toInstant(), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return ifNull;
        }
    }

    public static Date parse(String date, String pattern) {
        return Date.from(
                LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isToday(Date date) {
        if (Objects.isNull(date)) {
            return false;
        }
        return LocalDate.now().isEqual(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate());
    }

    public static long cd235959() {
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(now, end);
        return between.getSeconds();
    }

    public static String ttl(Date deadline) {
        LocalDateTime future = LocalDateTime.ofInstant(deadline.toInstant(), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, future);
        if (duration.getSeconds() <= 0) {
            return "即将结束";
        }
        return duration((int) duration.getSeconds());
    }

    public static void main(String[] args) {
        Date future = parse("2018-11-14 16:12:29", yyyyMMddHHmmss);
        System.out.println(ttl(future));
    }

    public static String duration(int duration) {
        int d = duration / 86400;
        int h = (duration / 3600) % 24;
        int m = (duration / 60) % 60;
        int s = duration % 60;

        if (d > 0) {
            return String.format("%d天%d时", d, h);
        }
        if (h > 0) {
            return String.format("%d时%d分", h, m);
        }
        if (m > 0) {
            return String.format("%d分%d秒", m, s);
        }
        return String.format("%2d秒", s);
    }


}
