package com.blackzheng.app.locator.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BlackZheng on 2017/2/18.
 */

public class DateUtil {
    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到毫秒的时间戳
     * @param format
     * @return
     */
    public static String timeStamp2Date(long seconds,String format) {
        if(seconds == 0l){
            return "0";
        }
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }
    /**
     * 日期格式字符串转换成时间戳
     * @param date 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long date2TimeStamp(String date,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static long timeStamp(){
        return System.currentTimeMillis();
    }
}
