package com.cyl.fitmanager.Appcontext;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cyl.fitmanager.R;
import com.cyl.fitmanager.Data.GroupProp;
import com.cyl.fitmanager.Constant;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.Calendar;
import java.util.Date;

import static com.cyl.fitmanager.Utils.parseDateInDay;

/**
 * 集成了Application类，放了些公用逻辑和数据库句柄，这样实现貌似不大好
 * Created by Administrator on 2016-2-3.
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public DB snappyDb;
    public SharedPreferences sp;
    @Override
    public void onCreate() {
        Log.e("fitmanager", "oncreate application for " + this);
        super.onCreate();
        try {
            snappyDb = DBFactory.open(this);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
    }

    public DB getDB() {
        return snappyDb;
    }

    public SharedPreferences getSP() {
        return sp;
    }

    public void putObjInSp(String key, Object obj) {
        sp.edit().putString(key, JSON.toJSONString(obj)).commit();
    }

    public <T> T getObjInSp(String key, Class<T> clazz) {
        return JSON.parseObject(sp.getString(key, null), clazz);
    }

    /**
     * 更新给定项目的下一训练日
     * @param program
     * @return
     */
    public String updateTrainingDay(String program) {
        try {
            GroupProp gp = getObjInSp(program + "_group",GroupProp.class);
            if (null == gp) gp = new GroupProp();
            Calendar cal = Calendar.getInstance();
            Date c_date = new Date();
            cal.setTime(c_date);
            for (int i = 0; i <= 7; i++) {
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int trainingBit = gp.trainingDayBitMap & (0x1 << dayOfWeek);
                if (trainingBit != 0) {
                    if (i != 0 || !parseDateInDay(c_date).equals(sp.getString("last_training_day_" + program, "-"))) return parseDateInDay(c_date);
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
    public void updateTrainingDay() {
        try {
            String training_day = "-";
            for (int i = 0; i < Constant.PROGRAMS.length; i++) {
                String program = Constant.PROGRAMS[i];
                String program_training_day = updateTrainingDay(program);
                sp.edit().putString("next_training_day_" + program, program_training_day).commit();
                Log.e("cyl", "next_training_day_" + program + " : " + program_training_day);
                if (training_day.equals("-")) training_day = program_training_day;
                else if (!training_day.equals("-") && !program_training_day.equals("-") && training_day.compareTo(program_training_day) > 0) {
                    training_day = program_training_day;
                }
            }
            Log.e("cyl", "next_training_day" + " : " + training_day);
            sp.edit().putString("next_training_day", training_day).commit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
