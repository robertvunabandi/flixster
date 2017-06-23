package com.codepath.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 6/22/17.
 */

public class Movie {
    // value from API
    private String title, overview, posterPath, backdropPath; // only path not full URL (for poster and backdrop)
    private float rating;

    public Movie (JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        rating = (float) (object.getDouble("popularity") * ((double) 6 / (double) 100));
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() { return backdropPath; }

    public float getRating() { return rating; }
}
