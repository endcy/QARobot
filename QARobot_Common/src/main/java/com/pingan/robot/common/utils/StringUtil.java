package com.pingan.robot.common.utils;

public class StringUtil {
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static String getUniqueId() {
        String base = String.valueOf(System.nanoTime());
        base = DateUtil.getCurrentDateTime() + base.substring(base.length() - 8, base.length());
        return base;
    }
}
