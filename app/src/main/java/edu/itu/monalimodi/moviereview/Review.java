package edu.itu.monalimodi.moviereview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Review implements Serializable {
    private static final long serialVersionUID = -8959832007991503784L;

    private String critic;
    private String criticDate;
    private String publication;
    private String score;
    private String quote;

    public static Review fromJson(JSONObject jsonObject) {
        Review r = new Review();
        try {
            r.critic = jsonObject.has("critic") ? jsonObject.getString("critic") : "";
            r.criticDate = jsonObject.has("date") ? jsonObject.getString("date") : "";
            r.publication = jsonObject.has("publication") ? jsonObject.getString("publication") : "";
            r.score = jsonObject.has("original_score") ? jsonObject.getString("original_score") : "";
            r.quote = jsonObject.has("quote") ? jsonObject.getString("quote") : "";
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }

        return r;
    }

    public static ArrayList<Review> fromJson(JSONArray jsonArray) {
        ArrayList<Review> businesses = new ArrayList<Review>(jsonArray.length());
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

            Review business = Review.fromJson(businessJson);
            if (business != null) {
                businesses.add(business);
            }
        }

        return businesses;
    }

    public static ArrayList<String> getReviewsAsString (ArrayList<Review> reviews) {
        ArrayList<String> result = new ArrayList<String>();
        Iterator itr = reviews.iterator();
        Review temp;
        String str;
        while(itr.hasNext()) {
            temp=(Review)itr.next();
            str=getReviewAsString(temp);
            result.add(str);
        }
        return result;
    }

    public static String getReviewAsString(Review r) {
        String result="";
        String cr = r.getCritic();
        String pub = r.getPublication();
        String date = r.getCriticDate();
        String scr = r.getScore();
        String quo = r.getQuote();

        if(cr.length() > 0)
            result += "Critic: " + r.getCritic() + "\n";
        if(pub.length()>0)
            result += "Publication: " + r.getPublication() + "\n";
        if(date.length()>0)
            result += "Date: " + r.getCriticDate() + "\n";
        if(scr.length()>0)
            result += "Score: " + r.getScore() + "\n";
        if(quo.length()>0)
            result += "Quote from critic: " + r.getQuote() + "\n";

        return result;
    }

    public String getCritic() {
        return critic;
    }

    public String getPublication() {
        return publication;
    }

    public String getCriticDate() {
        return criticDate;
    }

    public String getScore() {
        return score;
    }

    public String getQuote() {
        return quote;
    }

}
