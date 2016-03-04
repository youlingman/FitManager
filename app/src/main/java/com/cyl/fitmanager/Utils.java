package com.cyl.fitmanager;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 公用方法类
 * Created by Administrator on 2016-2-2.
 */
public class Utils {
    private Utils() {}
    /**
     * dp转为px
     * @param dp
     * @param context
     * @return
     */
    public static float dp2px(final float dp, final Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * px转为dp
     * @param px
     * @param context
     * @return
     */
    public static float px2dp(final float px, final Context context) {
        return px / (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * 获取当月的第一天对应的Date，保证返回满足equals()==0即compareTO()==0的Date实例
     * @return
     */
    public static java.util.Date firstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 将date对应的日期后移一个月
     * @param date
     * @return
     */
    public static Date forwardMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    /**
     * 将date对应的日期前移一个月
     * @param date
     * @return
     */
    public static Date previousMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * 根据指定格式格式化日期
     * @param format
     * @param date
     * @return
     */
    public static String parseDate(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 返回"yyyy-MM-dd"格式的日期
     * @param date
     * @return
     */
    public static String parseDateInDay(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 返回"yyyy-MM"格式的日期
     * @param date
     * @return
     */
    public static String parseDateInMonth(Date date) {
        return new SimpleDateFormat("yyyy-MM").format(date);
    }

    /**
     * 将给定秒数渲染成"hh-mm-ss"格式的字符串
     * @param seconds
     *             给定的秒数
     * @return
     */
    public static String parseSecondToTime(long seconds) {
        long hour = seconds / 3600;
        long minute = seconds / 60;
        long second = seconds % 60;
        String sHours = hour >= 10 ? "" + hour : "0" + hour;
        String sMinutes = minute >= 10 ? "" + minute : "0" + minute;
        String sSeconds = second >= 10 ? "" + second : "0" + second;
        return (sHours + ":" + sMinutes + ":" + sSeconds);
    }

    /**
     * 生成一个对应给定初始值、步长和容量的整型数组的字符串数组
     * @param firstVal
     *             初始值
     * @param step
     *             步长
     * @param size
     *             数组容量
     * @return
     */
    public static ArrayList<String> genArrayList(int firstVal, int step, int size) {
        ArrayList<String> array = new  ArrayList<>();
        int num = firstVal;
        for(int i = 0; i < size; i++) {
            array.add(num + "");
            num += step;
        }
        return array;
    }
}
