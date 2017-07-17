package com.cyl.fitmanager;

import android.content.Context;
import android.util.Log;

import com.cyl.fitmanager.Controller.DataController;
import com.cyl.fitmanager.Controller.ProgramConfigController;
import com.cyl.fitmanager.Model.GroupProp;

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

    /**
     * 更新给定项目的下一训练日
     * @param program
     * @return
     */
    public static String updateTrainingDay(String program) {
        ProgramConfigController programConfigController = ProgramConfigController.getInstance(program);
        try {
            GroupProp gp = programConfigController.getProp();
            if (null == gp) gp = new GroupProp();
            Calendar cal = Calendar.getInstance();
            Date c_date = new Date();
            cal.setTime(c_date);
            for (int i = 0; i <= 7; i++) {
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int trainingBit = gp.trainingDayBitMap & (0x1 << dayOfWeek);
                if (trainingBit != 0) {
                    if (i != 0 || !parseDateInDay(c_date).equals(programConfigController.getLastTrainingDay())) return parseDateInDay(c_date);
                }
                cal.add(Calendar.DATE, 1);
                c_date = cal.getTime();
            }
            return "-";
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    /**
     * 更新每个项目和总体对应的下一训练日
     */
    public static void updateTrainingDay() {
        DataController dataController = DataController.getInstance();
        try {
            String training_day = "-";
            for (int i = 0; i < Constant.PROGRAMS.length; i++) {
                String program = Constant.PROGRAMS[i];
                String program_training_day = updateTrainingDay(program);
                ProgramConfigController.getInstance(program).setNextTrainingDay(program_training_day);
                Log.e("cyl", "next_training_day_" + program + " : " + program_training_day);
                if (training_day.equals("-")) training_day = program_training_day;
                else if (!training_day.equals("-") && !program_training_day.equals("-") && training_day.compareTo(program_training_day) > 0) {
                    training_day = program_training_day;
                }
            }
            Log.e("cyl", "next_training_day" + " : " + training_day);
            dataController.getSP().edit().putString("next_training_day", training_day).commit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
