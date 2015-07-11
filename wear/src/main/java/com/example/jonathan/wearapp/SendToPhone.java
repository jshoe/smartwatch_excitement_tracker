package com.example.jonathan.wearapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

public class SendToPhone extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_phone);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        initiateApiClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
            Log.i("Activity", "Searching for nodes.");
            CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(
                    mGoogleApiClient, "take_pictures", CapabilityApi.FILTER_REACHABLE
            ).await();
            Set<Node> connected = result.getCapability().getNodes();
            for (Node node : connected) {
                bestNode = node.getId();
                Log.i("Activity", "Acquired a node.");
                sendMesgToPhone();
                Log.i("Activity", "Message was sent successfully");
                finish();
                break;
            }
            }
        }).start();
    }

    public void sendMesgToPhone() {
        Log.i("Activity", "Going to send a message.");
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, bestNode, "start_workflow", new byte[3]
        );
        Log.i("Activity", "Message sent successfully");
        finish();
    }

    private String bestNode = "1";
    private GoogleApiClient mGoogleApiClient;

    public void initiateApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i("Activity", "Connection between phone and watch successful.");
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

}
