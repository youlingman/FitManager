package com.cyl.fitmanager.Controller;

import android.text.TextUtils;

import com.cyl.fitmanager.Constant;
import com.snappydb.SnappydbException;

import java.util.Date;
import java.util.TreeSet;

/**
 * 训练统计数据相关接口
 * Created by Administrator on 2017-7-13.
 */
public class ProgramStatsController {
    private static String program;

    public static synchronized ProgramStatsController getInstance(String program) {
        ProgramStatsController instance = new ProgramStatsController();
        instance.changeProgram(program);
        return instance;
    }

    private ProgramStatsController() {
    }

    public void changeProgram(String program) {
        if(!TextUtils.isEmpty(program)) {
            this.program = program;
        }
    }

    /*
        相关数据的get/set的接口
    */
    // 训练月份数据
    public TreeSet<Date> getTrainingMonths() {
        try {
            return DataController.getInstance().getDB().get("training_months", TreeSet.class);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return new TreeSet<>();
        }
    }

    public void setTrainingMonths(TreeSet<Date> months) {
        try {
            DataController.getInstance().getDB().put(parseTrainingMonthsKey(), months);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    // 自由训练统计数据
    public int getFreeCount(String program, String date) {
        try {
            return DataController.getInstance().getDB().getInt(parseTrainingFreeCountKey(program, date));
        } catch (SnappydbException e) {
//            e.printStackTrace();
            return 0;
        }
    }

    public void setFreeCount(String program, String date, int count) {
        try {
            DataController.getInstance().getDB().putInt(parseTrainingFreeCountKey(program, date), count);;
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public int getProgramCount(String program, String date) {
        try {
            return DataController.getInstance().getDB().getInt(parseTrainingProgramCountKey(program, date));
        } catch (SnappydbException e) {
//            e.printStackTrace();
            return 0;
        }
    }

    public void setProgramCount(String program, String date, int count) {
        try {
            DataController.getInstance().getDB().putInt(parseTrainingProgramCountKey(program, date), count);;
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public int getTimeCount(String program, String date) {
        try {
            return DataController.getInstance().getDB().getInt(parseTrainingTimeCountKey(program, date));
        } catch (SnappydbException e) {
//            e.printStackTrace();
            return 0;
        }
    }

    public void setTimeCount(String program, String date, int count) {
        try {
            DataController.getInstance().getDB().putInt(parseTrainingTimeCountKey(program, date), count);;
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    /*
        拼接key部分
        获取数据在KV DB中的对应key
    */
    private String parseTrainingMonthsKey() {
        return Constant.TRAINING_MONTHS;
    }

    private String parseTrainingFreeCountKey(String program, String date) {
        return program + Constant.TRAINING_FREE_COUNT_POSTFIX + date;
    }

    private String parseTrainingProgramCountKey(String program, String date) {
        return program + Constant.TRAINING_PROGRAM_COUNT_POSTFIX + date;
    }

    private String parseTrainingTimeCountKey(String program, String date) {
        return program + Constant.TRAINING_TIME_COUNT_POSTFIX + date;
    }
}
