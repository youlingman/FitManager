package com.cyl.fitmanager.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.cyl.fitmanager.Appcontext.MainApplication;
import com.cyl.fitmanager.MainActivity;
import com.cyl.fitmanager.R;

import java.util.Date;

import static com.cyl.fitmanager.Utils.parseDateInDay;

/**
 * 查询当天是否为训练日，并弹出对应提醒信息
 * Created by Administrator on 2016-2-25.
 */
public class NotifyService extends IntentService {
    private static final String TAG = "NotifyService";
    // An ID used to post the notification.
    private static final int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private SharedPreferences sp;

    public NotifyService() {
        super("NotifyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        mNotification = new Notification.Builder(this)
                .setContentTitle("训练提醒")
                .setContentText("fit manager今天有未完成训练计划")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ((MainApplication)getApplication()).updateTrainingDay();
        String next_training = sp.getString("next_training_day", "-");
        if(next_training.equals(parseDateInDay(new Date()))) {
//            Log.e("fitmanager", "pop notification");
            // 弹出提醒信息
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, mNotification);
            wl.release();
        }
    }
}
