package com.cyl.fitmanager.Activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.cyl.fitmanager.R;

/**
 * Created by Administrator on 2016-1-15.
 */
public class SquatsMainActivity extends Activity implements SensorEventListener {
    private static final String TAG = "SquatsMainActivity";
    private long timestamp = 0;
    private float v = 0;
    private float s = 0;
    private float[] accelerometerValues = new float[3];
    private float[] linearaccelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mLinearAcc;
    private Sensor mMagnetic;
//    private double b_a1 = 0, b_a2 = 0, b_tan1 = 0, b_tan2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squats);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mLinearAcc, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ((TextView) findViewById(R.id.time)).setText("time:" + event.timestamp);
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            ((TextView) findViewById(R.id.acc)).setText("acc_x:" + x + "\nacc_y:" + y + "\nacc_z:" + z);
        }
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            linearaccelerometerValues = event.values;
            ((TextView) findViewById(R.id.gyo)).setText("lacc_x:" + event.values[0] + "\nlacc_y:" + event.values[1] + "\nlacc_z:" + event.values[2]);
        }
        float[] values = new float[3];
        float[] R_val = new float[9];
        SensorManager.getRotationMatrix(R_val, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R_val, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
//        ((TextView) findViewById(R.id.gyo)).setText("ori_x:" + values[0] + "\nori_y:" + values[1] + "\nori_z:" + values[2]);

        float g = 0;
//        String tmp = "";
        double a1 = Math.toRadians(values[2] + 90);
        double a2 = Math.toRadians(-values[1]);
        g += linearaccelerometerValues[2] * Math.sin(a1);
//        tmp += String.format("%f * %f = %f\n", accelerometerValues[2], values[2] + 90, accelerometerValues[2] * Math.sin(a1));
        g += linearaccelerometerValues[1] * Math.sin(a2);
//        tmp += String.format("%f * %f = %f\n", accelerometerValues[1], -values[1], accelerometerValues[1] * Math.sin(a2));
        g += linearaccelerometerValues[0] / Math.sqrt(1 + Math.pow(Math.tan(a1), 2) + Math.pow(Math.tan(a2), 2));
//        tmp += String.format("%f / sqrt(1 + tan(%f) ^ 2 + tan(%f) ^ 2) = %f\n", accelerometerValues[0], values[2] + 90, values[1], accelerometerValues[0] / Math.sqrt(1 + Math.pow(Math.tan(a1), 2) + Math.pow(Math.tan(a2), 2)));
        if (Math.abs(a1) <= 30 && Math.abs(a2) <= 20 && timestamp != 0) {
            long dt = (event.timestamp - timestamp);
//            v += ((g - SensorManager.GRAVITY_EARTH) * dt / 10000000);
            v += ((g) * dt / 10000000);
            s += (v * dt / 10000000);
        }
        ((TextView) findViewById(R.id.g)).setText("g:" + g + " v:" + v + " s:" + s);
        timestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
