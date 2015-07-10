package com.example.jonathan.wearapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.view.Menu;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
public class MainActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        startService(new Intent(getBaseContext(), ExciteService.class));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SendMesgToPhone();
        }

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void startService(View view) {
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), ExciteService.class));
    }

    private String bestNode = "1";

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Activity", "onNewIntent called");
        super.onNewIntent(intent);
    }

    public void SendMesgToPhone() {
        initiateApiClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //while(true) {
                Log.i("Activity", "Going to wait for getting capabilities");
                CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient, "take_pictures", CapabilityApi.FILTER_REACHABLE
                ).await();
                Set<Node> connected = result.getCapability().getNodes();
                for (Node node : connected) {
                    bestNode = node.getId();
                    Log.i("Activity", "Running at bestNode part");
                    break;
                }
                //}
            }
        }).start();

        Log.i("Activity", "Running MessageApi.sendMessage");
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, bestNode, "start_workflow", new byte[3]
        );
    }

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
