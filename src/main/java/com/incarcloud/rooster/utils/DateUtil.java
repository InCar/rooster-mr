package com.incarcloud.rooster.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * 在时间上增加分钟
     * @param date 时间
     * @param mins 分钟数
     * @return 增加后的时间
     */
    public static Date plusMinutes(Date date,int mins)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,mins);
        return calendar.getTime();
    }

    /**
     * 获取当天初始时间
     * @param date 时间
     * @return 初始时间 (yyyy-MM-dd 00:00:00)
     */
    public static Date getInitialTime(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String dateStr = dateFormat.format(date);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当天最后时间
     * @param date 时间
     * @return 最后时间 (yyyy-MM-dd 23:59:59)
     */
    public static Date getTerminalTime(Date date){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);
        dateStr = dateStr + " 23:59:59" ;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
