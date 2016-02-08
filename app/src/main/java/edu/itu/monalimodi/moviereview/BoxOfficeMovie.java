package edu.itu.monalimodi.moviereview;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoxOfficeMovie implements Serializable {
	private static final long serialVersionUID = -8959832007991513854L;
    private String id;
	private String title;
	private int year;
	private String synopsis;
	private String posterUrl;
	private String largePosterUrl;
	private String criticsConsensus;
    private String mpaaRating;
    private String releaseDate;
	private int audienceScore;

    private String UILink;
	private int criticsScore;
	private ArrayList<String> castList;
    private Map<String,String> characterList;

	// Returns a BoxOfficeMovie given the expected JSON
	// Reads `title`, `year`, `synopsis`, `posters.thumbnail`,
	// `ratings.critics_score` and the `abridged_cast`
	public static BoxOfficeMovie fromJson(JSONObject jsonObject) {
		BoxOfficeMovie b = new BoxOfficeMovie();
		try {
			//De-serialize json into object fields
            b.id = jsonObject.getString("id");
			b.title = jsonObject.getString("title");

            //year problem
            Object aYear = jsonObject.get("year");
            if(aYear instanceof Integer)
                b.year=(Integer) aYear;
            else
                b.year = -1;

			b.synopsis = jsonObject.getString("synopsis");
			b.posterUrl = jsonObject.getJSONObject("posters").getString("thumbnail");
			b.largePosterUrl = jsonObject.getJSONObject("posters").getString("detailed");
            if(jsonObject.has("critics_consensus"))
			    b.criticsConsensus = jsonObject.getString("critics_consensus");

            if(jsonObject.has("ratings")) {
                if(jsonObject.getJSONObject("ratings").has("critics_score"))
                    b.criticsScore = jsonObject.getJSONObject("ratings").getInt("critics_score");
                if(jsonObject.getJSONObject("ratings").has("audience_score"))
                    b.audienceScore = jsonObject.getJSONObject("ratings").getInt("audience_score");
            }

            if(jsonObject.has("release_dates") && jsonObject.getJSONObject("release_dates").has("theater"))
                b.releaseDate = jsonObject.getJSONObject("release_dates").getString("theater");

            if(jsonObject.has("mpaa_rating"))
                b.mpaaRating=jsonObject.getString("mpaa_rating");

            if(jsonObject.has("links") && jsonObject.getJSONObject("links").has("alternate"))
                b.UILink = jsonObject.getJSONObject("links").getString("alternate");

			// Construct simple array of cast names
			b.castList = new ArrayList<String>();
            b.characterList = new HashMap<String, String>();
			JSONArray abridgedCast = jsonObject.getJSONArray("abridged_cast");
			for (int i = 0; i < abridgedCast.length(); i++) {
				b.castList.add(abridgedCast.getJSONObject(i).getString("name"));

                if(abridgedCast.getJSONObject(i).has("characters"))
                    b.characterList.put(abridgedCast.getJSONObject(i).getString("name"), abridgedCast.getJSONObject(i).getString("characters") );
                else
                    b.characterList.put(abridgedCast.getJSONObject(i).getString("name"), "" );
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		// Return new object
		return b;
	}

	// Decodes array of box office movie json results into business model objects
	public static ArrayList<BoxOfficeMovie> fromJson(JSONArray jsonArray) {
		ArrayList<BoxOfficeMovie> businesses = new ArrayList<BoxOfficeMovie>(jsonArray.length());
		// Process each result in json array, decode and convert to business
		// object
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject businessJson;
			try {
				businessJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			BoxOfficeMovie business = BoxOfficeMovie.fromJson(businessJson);
			if (business != null) {
				businesses.add(business);
			}
		}

		return businesses;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public int getCriticsScore() {
		return criticsScore;
	}

	public String getCastList() {
		return TextUtils.join(", ", castList);
	}

    public String getCharacterList() {
        String result="";
        for (Map.Entry<String, String> entry : characterList.entrySet()) {
            String charName = entry.getValue().replaceAll("\\[", "").replaceAll("\\]","");
            if(charName!=null && charName.length()>0)
                result += entry.getKey() + " As " + charName + "<br />";
            else
                result += entry.getKey() + "<br />";
        }
        return result;
    }
	
	public String getLargePosterUrl() {
		return largePosterUrl;
	}

	public String getCriticsConsensus() {
		return criticsConsensus;
	}
	
	public int getAudienceScore() {
		return audienceScore;
	}

    public String getMpaaRating() { return mpaaRating; }

    public String getReleaseDate() { return releaseDate; }

    public String getId() { return id; }

    public String getUILink() { return UILink; }

}
