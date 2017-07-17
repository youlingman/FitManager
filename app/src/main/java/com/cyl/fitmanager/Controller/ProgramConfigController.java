package com.cyl.fitmanager.Controller;

import android.text.TextUtils;

import com.cyl.fitmanager.Constant;
import com.cyl.fitmanager.Model.GroupProp;

/**
 * Created by Administrator on 2017-7-11.
 * 提供对当前训练配置相关信息的获取和更新接口
 */
public class ProgramConfigController {
    private static String program;

    public static synchronized ProgramConfigController getInstance(String program) {
        ProgramConfigController instance = new ProgramConfigController();
        instance.changeProgram(program);
        return instance;
    }

    private ProgramConfigController() {
    }

    public void changeProgram(String program) {
        if(!TextUtils.isEmpty(program)) {
            this.program = program;
        }
    }

    /*
        相关数据的get/set的接口
    */
    // 训练项目配置GroupProp相关
    public GroupProp getProp() {
        return DataController.getInstance().getObjInSp(parseGroupKey(program), GroupProp.class);
    }

    public void setProp(GroupProp prop) {
        DataController.getInstance().putObjInSp(parseGroupKey(program), prop);
    }

    // 训练日部分
    public String getNextTrainingDay() {
        return DataController.getInstance().getSP().getString(parseNextTrainingDayKey(program), "-");
    }

    public void setNextTrainingDay(String nextTrainingDay) {
        DataController.getInstance().getSP().edit().putString(parseNextTrainingDayKey(program), nextTrainingDay).commit();
    }

    public String getLastTrainingDay() {
        return DataController.getInstance().getSP().getString(parseLastTrainingDayKey(program), "-");
    }

    public void setLastTrainingDay(String lastTrainingDay) {
        DataController.getInstance().getSP().edit().putString(parseLastTrainingDayKey(program), lastTrainingDay).commit();
    }

    // 训练提示部分
    public Boolean getTrainTipFlag() {
        return DataController.getInstance().getSP().getBoolean(parseTrainTipKey(program), true);
    }

    public void setTrainTipFlag(Boolean trainTipFlag) {
        DataController.getInstance().getSP().edit().putBoolean(parseTrainTipKey(program), trainTipFlag).commit();
    }

    /*
        拼接key部分
        获取数据在KV DB中的对应key
    */
    private String parseGroupKey(String program) {
        return program + Constant.PROGRAM_PROP_POSTFIX;
    }

    private String parseNextTrainingDayKey(String program) {
        return program + Constant.PROGRAM_NEXT_TRAININGDAY_POSTFIX;
    }

    private String parseLastTrainingDayKey(String program) {
        return program + Constant.PROGRAM_LAST_TRAININGDAY_POSTFIX;
    }

    private String parseTrainTipKey(String program) {
        return program + Constant.PROGRAM_TIP_FLAG_POSTFIX;
    }
}
