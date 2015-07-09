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
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

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

    private GetCapabilityResult nodes;
    private String bestNode = "1";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        initiateApiClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //while(true) {
                    Log.i("Wear", "Going to wait for getting capabilities");
                    CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(
                            mGoogleApiClient, "take_pictures", CapabilityApi.FILTER_REACHABLE
                    ).await();
                    Set<Node> connected = result.getCapability().getNodes();
                    for (Node node : connected) {
                        bestNode = node.getId();
                        Log.i("Wear", "Running at bestNode part");
                        break;
                    }
                //}
            }
        }).start();

        return START_STICKY;
    }

    private GoogleApiClient mGoogleApiClient;

    public void initiateApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i("Wear", "Connection between phone and watch successful.");
                    }
                    @Override
                    public void onConnectionSuspended( int i) {
                        // Do something
                    }
                })
                .addOnConnectionFailedListener( new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // Do something
                    }
                })
                .addApi(Wearable.API)
                .build();
        this.mGoogleApiClient.connect();
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
            showNotif();
        }
    }

    public void SendMesgToPhone() {
        Log.i("Wear", "Running MessageApi.sendMessage");
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, bestNode, "start_workflow", new byte[3]
        );
    }

    public void showNotif() {
        int notificationId = 001;

        // Build intent for notification content
        Intent userResponse = new Intent(this, SendToPhone.class);
        userResponse.putExtra("methodName", "SendMesgToPhone");

        // viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, userResponse, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.open_on_phone)
                .setContentTitle("Excitement!")
                .setContentText("Open to document it")
                .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
    }

}