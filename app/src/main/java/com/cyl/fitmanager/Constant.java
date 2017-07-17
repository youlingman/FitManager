package com.cyl.fitmanager;

import android.app.Fragment;

import com.cyl.fitmanager.Activity.PushUpMainActivity;
import com.cyl.fitmanager.Activity.SitUpMainActivity;
import com.cyl.fitmanager.Activity.SquatsMainActivity;
import com.cyl.fitmanager.Fragment.TrainChartFragment;
import com.cyl.fitmanager.Fragment.TrainConfigFragment;
import com.cyl.fitmanager.Fragment.TrainEntryFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量类
 * Created by Administrator on 2016-3-1.
 */
public class Constant {
    private Constant() {
    }
    // 训练项目相关配置
    public static final String PROGRAMS[] = {"深蹲", "俯卧撑", "仰卧起坐"};
    public static final Map<String, Class> PROGRAM_CLASS = new HashMap<String, Class>() {{
        put("深蹲", SquatsMainActivity.class);
        put("俯卧撑", PushUpMainActivity.class);
        put("仰卧起坐", SitUpMainActivity.class);
    }};
    public static final Map<String, Integer> PROGRAM_TIP = new HashMap<String, Integer>() {{
        put("深蹲", R.string.squats_tips);
        put("俯卧撑", R.string.pushup_tips);
        put("仰卧起坐", R.string.situp_tips);
    }};
    // 训练首页fragment
    public static final Fragment fragments[] = {TrainEntryFragment.newInstance(),  TrainConfigFragment.newInstance(), TrainChartFragment.newInstance()};
    /*
        数据key相关后缀
    */
    // 训练计划部分
    public static String PROGRAM_PROP_POSTFIX = "_group";
    public static String PROGRAM_NEXT_TRAININGDAY_POSTFIX = "_next_training_day";
    public static String PROGRAM_LAST_TRAININGDAY_POSTFIX = "_last_training_day";
    public static String PROGRAM_TIP_FLAG_POSTFIX = "_no_tip";

    // 训练统计部分
    public static String TRAINING_MONTHS = "training_months";
    public static String TRAINING_FREE_COUNT_POSTFIX = "_free_count_";
    public static String TRAINING_PROGRAM_COUNT_POSTFIX = "_program_count_";
    public static String TRAINING_TIME_COUNT_POSTFIX = "_time_count_";
}
