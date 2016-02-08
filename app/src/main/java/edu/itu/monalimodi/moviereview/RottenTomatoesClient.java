package edu.itu.monalimodi.moviereview;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RottenTomatoesClient {
	private final String API_KEY = "q4pqqqhjqpu2kvf2wjakehvv";
	private final String API_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/";
	private AsyncHttpClient client;

	public RottenTomatoesClient() {
		this.client = new AsyncHttpClient();
	}
	
	// http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey=<key>
	public void getBoxOfficeMovies(JsonHttpResponseHandler handler) {
		String url = getApiUrl("lists/movies/box_office.json");
		RequestParams params = new RequestParams("apikey", API_KEY);
        params.put("limit","16");
        client.get(url, params, handler);
	}

    //http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=hobbit&page_limit=50&page=1&apikey=xxxx
    public void searchMovies(String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("movies.json");
        RequestParams params = new RequestParams("q", query);
        params.put("page", "1");
        params.put("page_limit", "50");
        params.put("apikey", API_KEY);
        client.get(url, params, handler);
    }

    //http://api.rottentomatoes.com/api/public/v1.0/movies/771181360/reviews.json?review_type=top_critic&page_limit=50&page=1&country=us&apikey=XXXX
    public void getTopCritics(String id, JsonHttpResponseHandler handler) {
        String url = getApiUrl("movies/" + id + "/reviews.json");
        RequestParams params = new RequestParams("review_type", "top_critic");
        params.put("page_limit", "50");
        params.put("page", "1");
        params.put("apikey", API_KEY);
        client.get(url, params, handler);
    }

	private String getApiUrl(String relativeUrl) {
		return API_BASE_URL + relativeUrl;
	}
}