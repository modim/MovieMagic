package edu.itu.monalimodi.moviereview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class ListMoviesActivity extends Activity {

    private ListView lvMovies;
    private BoxOfficeMoviesAdapter adapterMovies;
    private RottenTomatoesClient client;
    public static final String MOVIE_DETAIL_KEY = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movies);

        Intent intent = getIntent();

        if(intent.getStringExtra("action").equals("search")) {
            String query = intent.getStringExtra("query");

            lvMovies = (ListView) findViewById(R.id.lvMovies);
            ArrayList<BoxOfficeMovie> aMovies = new ArrayList<BoxOfficeMovie>();
            adapterMovies = new BoxOfficeMoviesAdapter(this, aMovies);
            lvMovies.setAdapter(adapterMovies);
            // Fetch the data remotely
            fetchMovies(query);
            setupMovieSelectedListener();
        }

        if(intent.getStringExtra("action").equals("towatchlist")) {
            getActionBar().setTitle("To-Watch List");
            lvMovies = (ListView) findViewById(R.id.lvMovies);
            ArrayList<BoxOfficeMovie> aMovies = new ArrayList<BoxOfficeMovie>();
            adapterMovies = new BoxOfficeMoviesAdapter(this, aMovies);
            lvMovies.setAdapter(adapterMovies);
            fetchToWatchList();
            setupMovieSelectedListener();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if(intent.getStringExtra("action").equals("towatchlist")) {
            getActionBar().setTitle("To-Watch List");
            lvMovies = (ListView) findViewById(R.id.lvMovies);
            ArrayList<BoxOfficeMovie> aMovies = new ArrayList<BoxOfficeMovie>();
            adapterMovies = new BoxOfficeMoviesAdapter(this, aMovies);
            lvMovies.setAdapter(adapterMovies);
            fetchToWatchList();
            setupMovieSelectedListener();
        }
    }

    private void fetchToWatchList() {
        String FILENAME = "WatchList";
        ArrayList<BoxOfficeMovie> toWatchMovies=null;
        String path=getApplicationContext().getFilesDir().getAbsolutePath()+"/"+FILENAME;
        File file = new File(path);
        if(file.exists()) {
            //Toast.makeText(getApplicationContext(),"FILE exists at "+path, Toast.LENGTH_SHORT).show();
            try {
                FileInputStream fis = openFileInput(FILENAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                toWatchMovies = (ArrayList) ois.readObject();
                ois.close();
                fis.close();
            }catch (Exception ex) {
                Log.d(BoxOfficeDetailActivity.class.getName(), ex.getMessage());
                ex.printStackTrace();
            }
        }else {
            //Toast.makeText(getApplicationContext(),"FILE DOES NOT exits", Toast.LENGTH_SHORT).show();
            toWatchMovies = new ArrayList<BoxOfficeMovie>();
        }
        adapterMovies.addAll(toWatchMovies);
    }

    private void fetchMovies(String q){
        client = new RottenTomatoesClient();
        client.searchMovies(q, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, JSONObject body) {
                Log.d(BoxOfficeActivity.class.getName(), body.toString());
                JSONArray items = null;
                try {
                    // Get the movies json array
                    items = body.getJSONArray("movies");
                    // Parse json array into array of model objects
                    ArrayList<BoxOfficeMovie> movies = BoxOfficeMovie.fromJson(items);
                    if(movies.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No result found", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                finish();
                            }
                        };
                        handler.postDelayed(r,4000);
                    }
                    // Load model objects into the adapter which displays them
                    adapterMovies.addAll(movies);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setupMovieSelectedListener() {
        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                Intent i = new Intent(ListMoviesActivity.this, BoxOfficeDetailActivity.class);
                i.putExtra(MOVIE_DETAIL_KEY, adapterMovies.getItem(position));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_close) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
