package com.cyl.fitmanager;

import com.cyl.fitmanager.Activity.PushUpMainActivity;
import com.cyl.fitmanager.Activity.SitUpMainActivity;
import com.cyl.fitmanager.Activity.SquatsMainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量类
 * Created by Administrator on 2016-3-1.
 */
public class Constant {
    private Constant() {
    }

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
}
