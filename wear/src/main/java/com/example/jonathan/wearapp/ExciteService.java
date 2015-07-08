package com.example.jonathan.wearapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jonathan on 7/8/15.
 */
public class ExciteService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccel;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        Toast.makeText(this, "Accuracy Changed", Toast.LENGTH_SHORT).show();
    }

    private float lastValue = 0;

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float curValue = event.values[0];
        float diff = Math.abs(curValue - lastValue);
        lastValue = curValue;
        if (diff > 30) {
            Toast.makeText(this, "You're excited!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    protected void onResume() {
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
    }

}