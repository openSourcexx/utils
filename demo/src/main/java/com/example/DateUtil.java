package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当天开始时间
     * @param dt
     * @return
     */
    public static Date getDayBegin(Date dt) {
        if (dt == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            return cal.getTime();
        }
    }

    /**
     * 获取当天结束时间
     * @param dt
     * @return
     */
    public static Date getDayEnd(Date dt) {
        if (dt == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.set(11, 23);
            cal.set(12, 59);
            cal.set(13, 59);
            cal.set(14, 999);
            return cal.getTime();
        }
    }

    /**
     *     * 获得本周的第一天，周一
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentWeekDayStartTime() {
        Calendar c = Calendar.getInstance();
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
            c.add(Calendar.DATE, -weekday);
            c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }


    /**
     * 获得本周的最后一天，周日   
     */
    public static Date getCurrentWeekDayEndTime() {
        Calendar c = Calendar.getInstance();
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DATE, 8 - weekday);
            c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     *  获得本月的开始时间 
     */
    public static Date getCurrentMonthStartTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            now = shortSdf.parse(shortSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }


    /**
     *     * 当前月的结束时间
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentMonthEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     *     * 当前年的开始时间
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentYearStartTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DATE, 1);
            now = shortSdf.parse(shortSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }


    /**
     *     * 当前年的结束时间
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     *     * 当前季度的开始时间
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }


    /**
     *     * 当前季度的结束时间
     *     * 
     *     * @return
     *    
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获取2个时间间隔天数
     * @param before
     * @param after
     * @return
     */
    public static int getBetwweenDays(Date before, Date after) {
        try {
            before = shortSdf.parse(shortSdf.format(before));
            after = shortSdf.parse(shortSdf.format(after));
            Calendar cal = Calendar.getInstance();
            cal.setTime(before);
            long time1 = cal.getTimeInMillis();
            cal.setTime(after);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) /86400000L;
            int result = Integer.parseInt(String.valueOf(between_days));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 日期转字符串
     * 默认YYYY-MM-dd。
     * @return String "YYYY-MM-dd"格式的日期字符串。
     */
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * 日期转字符串
     */
    public static String getDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(date);
    }

    /**
     * 时间戳转字符串时间
     * @param timeStamp 时间戳
     * @param format
     * @return
     */
    public static String getDate(long timeStamp, String format) {
        Date date = new Date(timeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(date);
    }

    /**
     * 字符串时间转时间
     * @param dateStr 字符串时间
     * @param pattern 时间
     * @return
     */
    public static Date getDate(String dateStr, String pattern) {
        if (DateUtil.isDateTime(dateStr, pattern)) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            try {
                Date date = df.parse(dateStr);
                return date;
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * 判断字符串是否日期时间格式
     * @param str
     * @param pattern 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
     * @return
     */
    public static boolean isDateTime(String str, String pattern) {
        boolean convertSuccess = true;

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }


    public static void main(String[] args) {
        System.out.println(DateUtil.getCurrentMonthStartTime());
        System.out.println(DateUtil.getCurrentMonthEndTime());
        System.out.println(DateUtil.getCurrentQuarterStartTime());
        System.out.println(DateUtil.getCurrentQuarterEndTime());
        System.out.println(DateUtil.getCurrentYearStartTime());
        System.out.println(DateUtil.getCurrentYearEndTime());
        System.out.println(DateUtil.getCurrentWeekDayStartTime());
        System.out.println(DateUtil.getCurrentWeekDayEndTime());
        System.out.println(DateUtil.getDate(1547084941000l,"YYYY-MM-dd HH:mm:ss"));
    }

}
