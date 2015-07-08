package com.example.jonathan.wearapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.view.View;
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
            test();
        }
    }

    public void test() {
        int notificationId = 001;

        // Create a pending intent that starts this wearable app
        Intent startIntent = new Intent(this, MainActivity.class).setAction(Intent.ACTION_MAIN);
        // Add extra data for app startup or initialization, if available
        PendingIntent startPendingIntent =
                PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("hi")
                .setContentText("lol")
                .setContentIntent(startPendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notify);
    }

    public void btnShowNotificationClick(){
        int notificationId = 001;
        Context context = getApplicationContext();

        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("hi")
                        .setContentText("man")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
        Toast.makeText(this, "Well this part is working!", Toast.LENGTH_SHORT).show();
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