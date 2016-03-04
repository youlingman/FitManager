package com.cyl.fitmanager.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.util.Log;

/**
 * 响应开机事件设置定时任务
 * Created by Administrator on 2016-2-26.
 */
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarmRcv = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("fitmanager", "onBoot for alarm in fitmanager");
        alarmRcv.setAlarm(context);
    }
}
