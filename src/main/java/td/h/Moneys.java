package td.h;

import java.util.Currency;
import java.util.Locale;

public class Moneys {

    /**
     * 格式化，保留两位数
     *
     * @param cent 分
     * @return
     */
    public static String format(int cent) {
        return String.format("%.2f", (double) cent / 100);
    }

    /**
     * 格式化，千分位
     *
     * @param cent 分
     * @return
     */
    public static String format_pretty(int cent) {
        return String.format(Currency.getInstance(Locale.CHINA).getSymbol() + "%,.2f", (double) cent / 100);
    }

    public static void main(String[] args) {
        System.out.println(Moneys.format(-10132312));
    }
}
