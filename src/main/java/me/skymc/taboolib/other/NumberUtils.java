package me.skymc.taboolib.other;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author sky
 */
public class NumberUtils {

    private static Random random = new Random();
    private static DecimalFormat doubleFormat = new DecimalFormat("#.##");

    public static Random getRandom() {
        return random;
    }

    public static Double format(Double num) {
        return Double.valueOf(doubleFormat.format(num));
    }

    public static int getRandomInteger(Number num1, Number num2) {
        int min = Math.min(num1.intValue(), num2.intValue());
        int max = Math.max(num1.intValue(), num2.intValue());
        return (int) (random.nextDouble() * (max - min) + min);
    }

    public static double getRandomDouble(Number num1, Number num2) {
        double min = Math.min(num1.doubleValue(), num2.doubleValue());
        double max = Math.max(num1.doubleValue(), num2.doubleValue());
        return random.nextDouble() * (max - min) + min;
    }

    public static int getInteger(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDouble(String s) {
        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Boolean getBoolean(String s) {
        try {
            return Boolean.valueOf(s);
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean getBooleanAbbreviation(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        if (str.length() < 4) {
            char var = str.charAt(0);
            if (var == 'y' || var == 'Y' || var == 't' || var == 'T' || var == '1') {
                return true;
            }
            if (var == 'n' || var == 'N' || var == 'f' || var == 'F' || var == '0') {
                return false;
            }
        }
        return getBoolean(str);
    }

    @Deprecated
    public static Random getRand() {
        return random;
    }

    @Deprecated
    public static boolean getChance(int a) {
        return getRandom().nextInt(100) <= a;
    }
}
