package at.swimmesberger.bo2.profile.util;

import java.util.Locale;

public class ParseUtils {
    public static boolean objectToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return stringToBoolean(String.valueOf(value));
        }
    }

    public static int objectToInt(Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            return num.intValue();
        } else {
            return stringToInt(String.valueOf(value));
        }
    }

    public static long objectToLong(Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            return num.longValue();
        } else {
            return stringToLong(String.valueOf(value));
        }
    }

    public static double objectToDouble(Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            return num.doubleValue();
        } else {
            return stringToDouble(String.valueOf(value));
        }
    }

    public static int stringToInt(String text) {
        return Integer.parseInt(text);
    }

    public static long stringToLong(String text) {
        return Long.parseLong(text);
    }

    public static double stringToDouble(String text) {
        return Double.parseDouble(text);
    }

    public static boolean stringToBoolean(String text) {
        if (text.equals("0")) return false;
        if (text.toLowerCase(Locale.ENGLISH).equals("false")) return false;
        if (text.equals("1")) return true;
        if (text.toLowerCase(Locale.ENGLISH).equals("true")) return true;
        return false;
    }
}
