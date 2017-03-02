package com.incarcloud.rooster.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Incar on 2017/3/2.
 */
public class DateUtil {

    /**
     * 获取时间字符串
     * @param date 时间
     * @param format 时间格式
     * @return 字符串
     */
    public static String getDateStr(Date date, String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 功能描述：格式化日期
     * @param dateStr 字符型日期
     * @param format 日期格式
     * @return Date 日期
     */
    public static Date parseStrToDate(String dateStr, String format) {

        if(dateStr==null||"".equals(dateStr)){
            return  null;
        }

        Date date;
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
        return date;
    }
}
