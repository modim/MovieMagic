package edu.itu.monalimodi.moviereview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class ReviewsActivity extends Activity {
    private ListView lvReviews;
    private TextView tvLabel;
    private ArrayAdapter<String> reviewsAdapter;
    private RottenTomatoesClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        tvLabel = (TextView) findViewById(R.id.tvLabel);
        lvReviews = (ListView) findViewById(R.id.lvReviews);
        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        getActionBar().setTitle("Reviews of "+title);
        tvLabel.setText("Critic Reviews: " + title);

        ArrayList<String> aReviews = new ArrayList<String>();
        reviewsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aReviews);
        lvReviews.setAdapter(reviewsAdapter);

        fetchReviews(id);

    }

    protected void fetchReviews(String id) {
        client = new RottenTomatoesClient();
        client.getTopCritics(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, JSONObject body) {
                Log.d(BoxOfficeActivity.class.getName(), body.toString());
                JSONArray items = null;
                try {
                    // Get the movies json array
                    items = body.getJSONArray("reviews");
                    // Parse json array into array of model objects
                    ArrayList<Review> reviews = Review.fromJson(items);
                    if(reviews.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No reviews found", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                finish();
                            }
                        };
                        handler.postDelayed(r,4000);
                    }
                    reviewsAdapter.addAll(Review.getReviewsAsString(reviews));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reviews, menu);
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

        if(id == R.id.action_close) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

