package com.cyl.fitmanager.Activity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * 利用传感器计算出的手机角度变化识别仰卧起坐训练动作
 * Created by Administrator on 2016-1-19.
 */
public class SitUpMainActivity extends TrainingBaseActivity implements SensorEventListener {
    private static final String TAG = "SitUpMainActivity";
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] R_val = new float[9];
    private float[] values = new float[3];
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (UNAVAILABLE == trainingState) {
            return;
        }
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        SensorManager.getRotationMatrix(R_val, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R_val, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
        if (values[2] <= -120 || values[2] > 120) {
            trainingState = DOWN;
        } else if (values[2] >= -90 && values[2] <= 0 && DOWN == trainingState) {
            trainingState = UP;
            onSingleFinish();
        }
        if (count == 0) {
            onGroupFinish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
