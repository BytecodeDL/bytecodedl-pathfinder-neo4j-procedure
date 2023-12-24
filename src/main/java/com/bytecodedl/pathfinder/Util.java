package com.bytecodedl.pathfinder;

/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/24 10:20
 */
public class Util {
    public static Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
