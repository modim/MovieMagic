package edu.itu.monalimodi.moviereview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BoxOfficeDetailActivity extends Activity {
    private ImageView ivPosterImage;
    private TextView tvTitle;
    private TextView tvSynopsis;
    private TextView tvReleaseDate;
    private TextView tvMpaaRating;
    private TextView tvAudienceScore;
    private TextView tvCriticsScore;
    private TextView tvCriticsConsensus;

    private BoxOfficeMovie currentMovie;
    private Set<String> toWatchMovieIds;
    private final String MyPREFERENCES = "preferenceToWatchMovieIds";
    private final String KEY_MOVIE_IDS = "keyToWatchMovieIds";
    private SharedPreferences preferences;

    private ArrayList<BoxOfficeMovie> toWatchMovies;
    private final String FILENAME = "WatchList";

    private ShareActionProvider share=null;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_office_detail);
        // Fetch views
        ivPosterImage = (ImageView) findViewById(R.id.ivPosterImage);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvMpaaRating = (TextView) findViewById(R.id.tvMpaaRating);
        tvAudienceScore =  (TextView) findViewById(R.id.tvAudienceScore);
        tvCriticsScore = (TextView) findViewById(R.id.tvCriticsScore);

        //get all the to-watch list of movies
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        toWatchMovieIds = new HashSet<String>(preferences.getStringSet(KEY_MOVIE_IDS, new HashSet<String>()));

        //load all movies objects from file
        loadWatchList();

        // Load movie data
        BoxOfficeMovie movie = (BoxOfficeMovie) getIntent().getSerializableExtra("movie");
        currentMovie = movie;
        loadMovie(movie);
    }

    //load watchlist movies ArrayList
    public void loadWatchList() {
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
    }

    // Populate the data for the movie
    @SuppressLint("NewApi")
    public void loadMovie(BoxOfficeMovie movie) {
        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setTitle(movie.getTitle());
        }

        // Populate data
        tvTitle.setText(movie.getTitle());
        tvCriticsScore.setText(Html.fromHtml("<b>Critics Score:</b> " + movie.getCriticsScore() + "%"));
        tvAudienceScore.setText(Html.fromHtml("<b>Audience Score:</b> " + movie.getAudienceScore() + "%"));
        String release_date = movie.getReleaseDate();
        if(release_date!=null && release_date.length()>0)
            tvReleaseDate.setText(Html.fromHtml("<b>Release Date:</b> " + movie.getReleaseDate() ));
        else
            tvReleaseDate.setText(Html.fromHtml("<b>Release Date:</b> " + movie.getYear() ));
        tvMpaaRating.setText(Html.fromHtml("<b>MPAA Rating:</b> " + movie.getMpaaRating() ));

        String synopsisText = "<b>Synopsis:</b> " + movie.getSynopsis() + "<br /><br />";
        synopsisText+="<b>Cast and Characters:</b><br />" + movie.getCharacterList() + "<br /><br />";
        if(movie.getCriticsConsensus()!=null && movie.getCriticsConsensus().length()>0)
            synopsisText+="<b>Critics Consensus:</b> " + movie.getCriticsConsensus() + "<br /><br />";
        tvSynopsis.setText(Html.fromHtml(synopsisText));

        // R.drawable.large_movie_poster from
        // http://content8.flixster.com/movie/11/15/86/11158674_pro.jpg -->
        Picasso.with(this).load(movie.getLargePosterUrl()).
                placeholder(R.drawable.large_movie_poster).
                fit().centerCrop().
                into(ivPosterImage);

        //add reviews link to open new intent
        Button reviewButton = (Button) findViewById(R.id.buttonReviews);
        final String movieID = movie.getId();
        final String movieTitle = movie.getTitle();

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), ReviewsActivity.class);
                myIntent.putExtra("id", movieID);
                myIntent.putExtra("title", movieTitle);
                startActivity(myIntent);
            }
        });


        //add button's click event for UI
        Button linkButton = (Button) findViewById(R.id.buttonRotten);
        final String linkToUI = movie.getUILink();
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent internetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkToUI));
                internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(internetIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.box_office_detail, menu);

        MenuItem watchItem = menu.findItem(R.id.action_watch_list);
        //Log.d("----->",toWatchMovieIds.toString());
        if(toWatchMovieIds.contains(currentMovie.getId())) {
            watchItem.setChecked(true);
            watchItem.setIcon(android.R.drawable.star_big_on);
        }

        //share option
        MenuItem menuItem = menu.findItem(R.id.share);
        share = (ShareActionProvider) menuItem.getActionProvider();
        shareIntent.setType("text/plain");
        if (share != null ) {
            String dataToShare = "Title: " + currentMovie.getTitle() + "\n";
            dataToShare+= "Critics Score: " + currentMovie.getCriticsScore() + "%\n";
            dataToShare+= "Cast: " + currentMovie.getCastList() + "\n";
            dataToShare+= "More information: " + currentMovie.getUILink() + "\n";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie: " + currentMovie.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
            share.setShareIntent(shareIntent);
        } else {
            Log.d(BoxOfficeDetailActivity.class.getName(), "Share Action Provider is null");
        }

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

        if(id == R.id.action_watch_list) {
            SharedPreferences.Editor editor = preferences.edit();
            if(item.isChecked()) {
                Toast.makeText(getApplicationContext(), "Removed from To-Watch list", Toast.LENGTH_SHORT).show();
                item.setChecked(false);
                item.setIcon(android.R.drawable.star_big_off);
                toWatchMovieIds.remove(currentMovie.getId());
                //toWatchMovies.remove(currentMovie);  //not working
                Iterator<BoxOfficeMovie> itr = toWatchMovies.iterator();
                String current_id = currentMovie.getId();
                while(itr.hasNext()) {
                    BoxOfficeMovie tmp = itr.next();
                    if(tmp.getId().equals(current_id))
                        itr.remove();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Added to To-Watch list", Toast.LENGTH_SHORT).show();
                item.setChecked(true);
                item.setIcon(android.R.drawable.star_big_on);
                toWatchMovieIds.add(currentMovie.getId());
                toWatchMovies.add(currentMovie);
            }
            editor.putStringSet(KEY_MOVIE_IDS, toWatchMovieIds);
            editor.commit();

            try {
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(toWatchMovies);
                oos.flush();
                oos.close();
                fos.close();

            }catch (Exception ex) {
                Log.d(BoxOfficeDetailActivity.class.getName(), ex.getMessage());
                ex.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
