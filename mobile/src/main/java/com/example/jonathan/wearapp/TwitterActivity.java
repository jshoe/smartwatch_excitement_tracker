package com.example.jonathan.wearapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;


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

                                Bitmap bitmap = getBitmapFromURL(src);

                                /**NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(TwitterActivity.this)
                                                .setSmallIcon(R.drawable.ic_media_play)
                                                .setContentTitle("Title")
                                                .setContentText("Android Wear Notification")
                                                .extend(new NotificationCompat.WearableExtender().setBackground(bitmap));

                                NotificationManagerCompat notificationManager =
                                        NotificationManagerCompat.from(TwitterActivity.this);

                                notificationManager.notify(2, notificationBuilder.build());**/
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

    public static Bitmap getBitmapFromURL(String src) {
        Log.i("TwitterActivityGuest", "Trying to make the bitmap.");
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    /**private Bitmap nextPic;

    public static Bitmap getBitmapFromURL(String src) {
        final String src2 = src;
        Bitmap nextPic2 = nextPic;
        new Thread(new Runnable() {
            @Override
                URL url = new URL(src2);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                nextPic2 = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    // Log exception
                }
            }
        }).start();
    }**/

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
