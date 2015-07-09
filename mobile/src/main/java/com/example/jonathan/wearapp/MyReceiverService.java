package com.example.jonathan.wearapp;

import android.content.Intent;
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
        Log.i("Phone", "Phone received the message.");
        Intent i = new Intent();
        i.setClass(this, PhotoWorkflowActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
