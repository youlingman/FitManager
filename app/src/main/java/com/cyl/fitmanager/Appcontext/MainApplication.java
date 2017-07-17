package com.cyl.fitmanager.Appcontext;

import android.app.Application;
import android.util.Log;

import com.cyl.fitmanager.Controller.DataController;
import com.snappydb.SnappydbException;

/**
 * 集成了Application类
 * Created by Administrator on 2016-2-3.
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        Log.e("fitmanager", "oncreate application for " + this);
        super.onCreate();
        try {
            DataController.getInstance().initDB(this);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
