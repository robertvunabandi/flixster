package com.codepath.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 6/22/17.
 */

public class Config {
    // BASE URL FOR IMAGES, AND
    String imageBaseUrl;
    String posterSize; // POSTER SIZE USED FOR WHEN FETCHING IMAGES
    String backdropSize; // USED FOR FETCHING DATA IN LANDSCAPE MODE

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images"); // get the image
        imageBaseUrl = (String) images.get("secure_base_url"); // secure URL
        JSONArray posterSizeOptions = (JSONArray) images.get("poster_sizes");
        posterSize = posterSizeOptions.optString(3, "w342"); // poster size
        JSONArray backdropSizeOptions = (JSONArray) images.get("backdrop_sizes"); // backdrop size options
        backdropSize = backdropSizeOptions.optString(1, "w780"); //choose size 2
    }

    // helper method to construct urls
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path); // concatenate all
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() { return backdropSize; }
}
