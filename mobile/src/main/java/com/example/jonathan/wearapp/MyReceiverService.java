package com.example.jonathan.wearapp;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by jonathan on 7/8/15.
 */
public class MyReceiverService extends WearableListenerService {
    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("Yo", "Got a message");
    }
}
