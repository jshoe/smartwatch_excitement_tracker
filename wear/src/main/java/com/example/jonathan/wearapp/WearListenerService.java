package com.example.jonathan.wearapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearListenerService extends WearableListenerService {
    public WearListenerService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i("WearListener", "The listener on the watch was created.");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("WearListener", "Wear received the message back.");
        Intent i = new Intent();
        i.setClass(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
