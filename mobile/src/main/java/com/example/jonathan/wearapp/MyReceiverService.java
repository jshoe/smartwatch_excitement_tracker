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

    String wearNodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        wearNodeId = messageEvent.getSourceNodeId();
        Log.i("Phone", "Phone received the message.");
        Log.i("Wear", "nodeID is " + wearNodeId);
        Intent i = new Intent();
        i.putExtra("wearNodeId", wearNodeId);
        i.setClass(this, PhotoWorkflowActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
