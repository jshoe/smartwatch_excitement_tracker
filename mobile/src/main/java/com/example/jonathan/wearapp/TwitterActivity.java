package com.example.jonathan.wearapp;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
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

        final SearchTimeline searchTimeline = new SearchTimeline.Builder().query("#twitterflock").build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(this, searchTimeline);
        //setListAdapter(adapter);

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
                        Log.i("TwitterActivityGuest", "Result: " + result.data.tweets.get(0).entities.media.get(0).mediaUrl);
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

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService service = twitterApiClient.getSearchService();
        service.tweets("#cs160excited", null, null, null, "recent", 1, null, null, null, true, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                Log.i("TwitterActivity", "It thinks it successfully searched.");
                Log.i("TwitterActivity", "Result: " + result.data.toString());
                Log.i("TwitterActivity", "Result: " + result.data.tweets.get(0).text);
                Log.i("TwitterActivity", "Result: " + result.data.tweets.get(0).entities.media.get(0).mediaUrl);
            }

            public void failure(TwitterException exception) {
                Log.i("TwitterActivity", "Problem searching.");
                Log.i("TwitterActivity", exception.toString());
            }
        });

        /**StatusesService statusesService = Twitter.getApiClient().getStatusesService();
        statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Log.i("TwitterActivity", "It thinks it successfully showed a status.");
                Log.i("TwitterActivity", "Result: " + result.data.text);
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });**/

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
