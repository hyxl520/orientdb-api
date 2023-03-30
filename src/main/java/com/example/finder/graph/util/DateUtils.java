
package com.example.finder.graph.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类 提供常用的日期方法
 *
 * @Author sccl
 * 
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH",
            "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM.dd HH", "yyyy.MM", "yyyy年MM月dd日",
            "yyyy年MM月dd日 HH时mm分ss秒", "yyyy年MM月dd日 HH时mm分", "yyyy年MM月dd日 HH时", "yyyy年MM月", "yyyy" };
    
    
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat FOMATE_MILLI_SECOND = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static final SimpleDateFormat FORMAT_SECOND = new SimpleDateFormat("yyyyMMddHHmmss");
    
    public static final SimpleDateFormat CUSTOM_DATE_FORMATE = new SimpleDateFormat();

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 得到日期字符串 ，转换格式（yyyy-MM-dd）
     */
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(long dateTime, String pattern) {
        return formatDate(new Date(dateTime), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, String pattern) {
        String formatDate = null;
        if (date != null) {
//          if (StringUtils.isNotBlank(pattern)) {
//              formatDate = DateFormatUtils.format(date, pattern);
//          } else {
//              formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
//          }
            if (StringUtils.isBlank(pattern)) {
                pattern = "yyyy-MM-dd";
            }
            formatDate = FastDateFormat.getInstance(pattern).format(date);
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return FastDateFormat.getInstance(pattern).format(new Date());
    }

    /**
     * 得到当前日期前后多少天，月，年的日期字符串
     * 
     * @param pattern 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     * @param amont 数量，前为负数，后为正数
     * @param type 类型，可参考Calendar的常量(如：Calendar.HOUR、Calendar.MINUTE、Calendar.SECOND)
     * @return
     */
    public static String getDate(String pattern, int amont, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(type, amont);
//      return DateFormatUtils.format(calendar.getTime(), pattern);
        return FastDateFormat.getInstance(pattern).format(calendar.getTime());
    }



    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }
    /**
     * 得到当前年份字符串 格式（yyyy）
     * @param date 指定日期
     */
    public static String getYear(Date date) {
        return formatDate(date, "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }
    /**
     * 得到当前月份字符串 格式（MM）
     * @param date 指定日期
     */
    public static String getMonth(Date date) {
        return formatDate(date, "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当天字符串 格式（dd）
     * @param date 指定日期
     */
    public static String getDay(Date date) {
        return formatDate(date, "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式 see to DateUtils#parsePatterns
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     * 
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     * 
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     * 
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 获取两个日期之间的天数
     * 
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * 获取某月有几天
     * 
     * @param date 日期
     * @return 天数
     */
    public static int getMonthHasDays(Date date) {
//      String yyyyMM = new SimpleDateFormat("yyyyMM").format(date);
        String yyyyMM = FastDateFormat.getInstance("yyyyMM").format(date);
        String year = yyyyMM.substring(0, 4);
        String month = yyyyMM.substring(4, 6);
        String day31 = ",01,03,05,07,08,10,12,";
        String day30 = "04,06,09,11";
        int day = 0;
        if (day31.contains(month)) {
            day = 31;
        } else if (day30.contains(month)) {
            day = 30;
        } else {
            int y = Integer.parseInt(year);
            if ((y % 4 == 0 && (y % 100 != 0)) || y % 400 == 0) {
                day = 29;
            } else {
                day = 28;
            }
        }
        return day;
    }

    /**
     * 获取日期是当年的第几周
     * 
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取一天的开始时间（如：2015-11-3 00:00:00.000）
     * 
     * @param date 日期
     * @return
     */
    public static Date getOfDayFirst(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的最后时间（如：2015-11-3 23:59:59.999）
     * 
     * @param date 日期
     * @return
     */
    public static Date getOfDayLast(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取服务器启动时间
     * 
     * @param date
     * @return
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 格式化为日期范围字符串
     * 
     * @param beginDate 2018-01-01
     * @param endDate 2018-01-31
     * @return 2018-01-01 ~ 2018-01-31
     * @author sccl
     */
    public static String formatDateBetweenString(Date beginDate, Date endDate) {
        String begin = DateUtils.formatDate(beginDate);
        String end = DateUtils.formatDate(endDate);
        if (StringUtils.isNoneBlank(begin, end)) {
            return begin + " ~ " + end;
        }
        return null;
    }

    /**
     * 解析日期范围字符串为日期对象
     * 
     * @param dateString 2018-01-01 ~ 2018-01-31
     * @return new Date[]{2018-01-01, 2018-01-31}
     * @author sccl
     */
    public static Date[] parseDateBetweenString(String dateString) {
        Date beginDate = null;
        Date endDate = null;
        if (StringUtils.isNotBlank(dateString)) {
            String[] ss = StringUtils.split(dateString, "~");
            if (ss != null && ss.length == 2) {
                String begin = StringUtils.trim(ss[0]);
                String end = StringUtils.trim(ss[1]);
                if (StringUtils.isNoneBlank(begin, end)) {
                    beginDate = DateUtils.parseDate(begin);
                    endDate = DateUtils.parseDate(end);
                }
            }
        }
        return new Date[] { beginDate, endDate };
    }
    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }
    
    
    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }
    
    public static final String dateMillisecondTimeNow()
    {
        return dateTimeNow("yyyyMMddHHmmssSSS");
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }
    /**
     * 格式化Timestamp
     * @Title: Timestamp
     * @author: UFO
     * @date: 2020年8月21日	上午10:49:40
     * @param time java.sql.Timestamp
     * @param pattern ps:yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws:
     */
    public static String formatDate(Timestamp time,String pattern){
    	if(time==null)
    		return null;
    	CUSTOM_DATE_FORMATE.applyPattern(pattern);
    	return CUSTOM_DATE_FORMATE.format(time);
    }

    /**
     * 获取当前日期的上个月，格式 yyyy-MM
     * 例：当前日期：2022-1-1日 返回 2021-12-01
     * @return
     */
    public static Date getLastMonthDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取指定日期的上个月，格式 yyyy-MM
     * 例：当前日期：2022-1-1日 返回 2021-12-01
     * @return
     */
    public static Date getLastMonthDateByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设置为上一个月
        calendar.add(Calendar.MONTH,-1);
        date = calendar.getTime();
        return date;
    }



    /**
     * 获取指定日期的月的第一天
     * 例：当前日期：2022-1-12  返回 2022-01-01 00:00:00
     * @return
     */
    public static String getFirstDayByDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String thisMonthFirstTime = format.format(calendar.getTime()) + " 00:00:00";
        return thisMonthFirstTime;
    }

    /**
     * 获取指定日期的月的最后一天
     * 例：当前日期：2022-1-12  返回 2022-01-31 23:59:59
     * @return
     */
    public static String getEndDayByDate(Date date) {
        // 本月末尾
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String thisMonthEndTime = format.format(calendar.getTime()) + " 23:59:59";
        return thisMonthEndTime;
    }

    public static final Date getYesterday()
    {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -1);
        return    cal.getTime();
    }
}
