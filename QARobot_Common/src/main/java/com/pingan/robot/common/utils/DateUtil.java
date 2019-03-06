package com.pingan.robot.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cxx on 2018/11/27.
 */
public class DateUtil {

    /**
     * 获取当前日期：默认格式为yyyyMMdd
     *
     * @return String
     */
    public static String getCurrentDate() {
        return getCurrentDateTime("yyyyMMdd");
    }

    /**
     * 获取当前时间：默认格式为HHmmss
     *
     * @return String
     */
    public static String getCurrentTime() {
        return getCurrentDateTime("HHmmss");
    }

    /**
     * 获取当前日期时间：默认格式为yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime("yyyyMMddHHmmss");
    }

    /**
     * 获取当前日期时间：默认格式为yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getCurrentDateTimeStd() {
        return getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 获取当前日期时间
     *
     * @param format 格式化日期的格式 如：yyyy-mm-dd HH:mm:ss
     * @return String
     */
    public static String getCurrentDateTime(String format) {
        return getDateStr(new Date(), format);
    }

    /**
     * 日期转换成字符串
     *
     * @param dateToFormat Date
     * @param format       String
     * @return String
     */
    public static String getDateStr(Date dateToFormat, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(dateToFormat);
    }

    /**
     * 将时间字符串格式化成yyyyMMddHHmmss 的形式
     *
     * @param strDateTime 要格式化的时间字符串
     * @param format      要格式化的时间字符串的格式
     * @return String
     * @throws ParseException
     */
    public static String formatDateTime(String strDateTime, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = formatter.parse(strDateTime);
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    /**
     * 获取下一个日期天
     *
     * @param date
     * @return String
     */
    public static String getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        date = calendar.getTime();
        return getDateStr(date, "yyyyMMdd");
    }

}

