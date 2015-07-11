package com.example.jonathan.wearapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TwitterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        getActionBar().setTitle("Excitement Assistant!");
        String imagePath = getIntent().getStringExtra("imagePath");
        Log.i("TwitterActivity", "Booting up Twitter.");
        Log.i("TwitterActivity", "Trying to get " + imagePath);
        File imageFile = new File(imagePath);
        Uri myImageUri = Uri.fromFile(imageFile);

        Intent intent = new TweetComposer.Builder(this)
                .text("#cs160excited ")
                .image(myImageUri)
                .createIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TwitterActivity", "In onActivityResult.");
        fetchMutualTweet();
    }

    public void fetchMutualTweet() {
        new Thread() {
            @Override
            public void run() {
                TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                    @Override
                    public void success(Result<AppSession> result) {
                        AppSession guestAppSession = result.data;
                        Log.i("TwitterActivity", "Guest session success.");
                        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(guestAppSession);
                        SearchService service = twitterApiClient.getSearchService();
                        service.tweets("#cs160excited", null, null, null, "recent", 1, null, null, null, true, new Callback<Search>() {
                            @Override
                            public void success(Result<Search> result) {
                                Log.i("TwitterActivityGuest", "It thinks it successfully searched.");
                                Log.i("TwitterActivityGuest", "Result: " + result.data.toString());
                                Log.i("TwitterActivityGuest", "Result: " + result.data.tweets.get(0).text);
                                String src = result.data.tweets.get(0).entities.media.get(0).mediaUrl;
                                Log.i("TwitterActivityGuest", "Result: " + src);

                                Bitmap pic = pictureFetch(src);
                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.nearby_icon);

                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(TwitterActivity.this)
                                                .setSmallIcon(R.drawable.nearby_icon)
                                                .setLargeIcon(icon)
                                                .setContentTitle("Nearby!")
                                                .setContentText("Someone else was excited!")
                                                .extend(new NotificationCompat.WearableExtender().setBackground(pic));
                                NotificationManagerCompat notificationManager =
                                        NotificationManagerCompat.from(TwitterActivity.this);
                                notificationManager.notify(2, notificationBuilder.build());
                            }

                            public void failure(TwitterException exception) {
                                Log.i("TwitterActivityGuest", "Problem searching.");
                                Log.i("TwitterActivityGuest", exception.toString());
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.i("TwitterActivityGuest", "Guest session failure.");
                    }
                });
            }
        }.start();
    }

    private Bitmap pictureFetch(final String src) {
        // http://developer.android.com/reference/java/util/concurrent/Executors.html
        // Fixes the network on main UI thread error.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Bitmap> result = executor.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                try {
                    return BitmapFactory.decodeStream((InputStream) new URL(src).getContent());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        try {
            return result.get();
        } catch (Exception e) {
            return null;
        }
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
