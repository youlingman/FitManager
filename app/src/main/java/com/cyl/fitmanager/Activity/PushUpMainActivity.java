package com.cyl.fitmanager.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 利用距离传感器识别俯卧撑训练动作
 * Created by Administrator on 2016-1-18.
 */
public class PushUpMainActivity extends TrainingBaseActivity implements SensorEventListener {
    private static final String TAG = "PushUpMainActivity";
    private SensorManager mSensorManager;
    private Sensor mProximity;
    final private int DOWN_THRESHOLE = 2;
    final private int UP_THRESHOLE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (State.UNAVAILABLE == trainingState) {
            return;
        }
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            Log.e("cyl", "distance:" + distance + " state:" + trainingState);
            if (distance <= DOWN_THRESHOLE && State.DOWN != trainingState) {
                trainingState = State.DOWN;
                onSingleFinish();
            } else if (distance > UP_THRESHOLE) {
                trainingState = State.UP;
            }
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
