package com.cyl.fitmanager.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.cyl.fitmanager.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * 数据存取单例，提供业务无关的数据接口
 * Created by Administrator on 2017-7-10.
 */
public class DataController {
    private DB snappyDb;
    private SharedPreferences sp;
    private static DataController instance;

    private DataController() {

    }

    public static synchronized DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    public void initDB(Context context) throws SnappydbException {
        sp = context.getSharedPreferences(context.getString(R.string.app_name), context.MODE_PRIVATE);
        snappyDb = DBFactory.open(context);
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
}
