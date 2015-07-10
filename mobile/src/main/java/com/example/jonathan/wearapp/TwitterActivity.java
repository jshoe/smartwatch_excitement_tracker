package com.example.jonathan.wearapp;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;


public class TwitterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        String imagePath = getIntent().getStringExtra("imagePath");
        Log.i("TwitterActivity", "Booting up Twitter.");
        Log.i("TwitterActivity", "Trying to get " + imagePath);
        File imageFile = new File(imagePath);
        Uri myImageUri = Uri.fromFile(imageFile);
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("#cs160excited ")
                .image(myImageUri);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
