package com.example.jonathan.wearapp;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
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

import java.io.File;
import java.util.concurrent.TimeUnit;
import android.content.Context;

public class TwitterActivity extends Activity {

    String wearNodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        String imagePath = getIntent().getStringExtra("imagePath");
        wearNodeId = getIntent().getStringExtra("wearNodeId");
        Log.i("TwitterActivity", "The wearNodeId is " + wearNodeId);
        Log.i("TwitterActivity", "Booting up Twitter.");
        Log.i("TwitterActivity", "Trying to get " + imagePath);
        File imageFile = new File(imagePath);
        Uri myImageUri = Uri.fromFile(imageFile);
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("#cs160excited ")
                .image(myImageUri);
        builder.show();

        final SearchTimeline searchTimeline = new SearchTimeline.Builder().query("#twitterflock").build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(this, searchTimeline);
        //setListAdapter(adapter);

        replyToWear("reply to wear");

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final SearchService service = twitterApiClient.getSearchService();
        service.tweets("#cs160excited", null, null, null, "recent", 1, null, null, null, true, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                Log.i("TwitterActivity", "It thinks it successfully searched.");
                Log.i("TwitterActivity", "Result: " + result.data.toString());
                Log.i("TwitterActivity", "Result: " + result.data.tweets.get(0).text);
                Log.i("TwitterActivity", "Result: " + result.data.tweets.get(0).entities.media.get(0).mediaUrl); // This is the right image URL.
            }

            public void failure(TwitterException exception) {
                Log.i("TwitterActivity", "Exception: " + exception.toString() + exception.getMessage());
                Log.i("TwitterActivity", "Failed to search for some reason.");
            }
        });
    }

    private void replyToWear(String message) {
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        Log.i("TwitterActivity", "Trying to send the reply to wear.");
        Wearable.MessageApi.sendMessage(client, wearNodeId, "reply to wear", null);
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
