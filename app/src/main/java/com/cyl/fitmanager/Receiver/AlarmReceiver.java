package com.cyl.fitmanager.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cyl.fitmanager.Service.NotifyService;

import java.util.Calendar;

/**
 * 每天9:00和21:00定时启动{@link NotifyService}
 * Created by Administrator on 2016-2-26.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("fitmanager", "on broadcast receive");
        context.startService(new Intent(context, NotifyService.class));
    }

    /**
     * set the alarm
     *
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, AlarmReceiver.class);
        if (PendingIntent.getBroadcast(context, 0, mIntent, PendingIntent.FLAG_NO_CREATE) == null) {
            Log.e("fitmanager", "set alarm");
            alarmIntent = PendingIntent.getBroadcast(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // Set the alarm's trigger time to 9:00 p.m.
            calendar.set(Calendar.HOUR_OF_DAY, 21);
            calendar.set(Calendar.MINUTE, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, alarmIntent);
        }
    }

    /**
     * cancel the alarm
     *
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
