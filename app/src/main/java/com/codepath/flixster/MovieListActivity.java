package com.codepath.flixster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.flixster.models.Config;
import com.codepath.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MovieListActivity extends AppCompatActivity {
    // CONSTANTS
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // PARAMETERS
    public final static String API_KEY_PARAM = "api_key";
    public final static String API_PAGE_PARAM = "page";
    // TAG FOR LOGGING FROM THIS ACTIVITY
    public final static String TAG = "MovieListActivity";
    // BASE URL FOR IMAGES, AND POSTER SIZE USED FOR WHEN FETCHING IMAGES
    //**String imageBaseUrl;
    //**String posterSize;
    int page = 1, maxPage;
    // LIST OF CURRENTLY PLAYING MOVIES
    ArrayList<Movie> movies;
    // THE RECYCLER VIEW
     RecyclerView rvMovies;
    // THE ADAPTER WIRED TO RECYCLER VIEW
    MovieAdapter adapter;
    // image config
    Config config;
    // buttons
    Button nextPage, previousPage;
    // text view for page
    TextView pageText;

    // instance fields (associated with specific movie listing activities)
    AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // Initialize the client
        client = new AsyncHttpClient();
        // initialize next and previous page buttons
        nextPage = (Button) findViewById(R.id.nextPage);
        previousPage = (Button) findViewById(R.id.prevPage);
        pageText = (TextView) findViewById(R.id.pageNumber);
        pageText.setText(Integer.toString(page));

        // see the end
        setMainVariables();

        // get configuration on app creation
        getConfiguration();
    }

    // get the list of currently playing movies
    private void getNowPlaying() {
        String url = API_BASE_URL + "/movie/now_playing";
        // set request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // api key always required
        params.put(API_PAGE_PARAM, String.valueOf(page)); // page
        // execute a get request expecting a JSON response containing list of now playing movies
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results in movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through results set and create Movie objects
                    maxPage = response.getInt("total_pages");
                    page = response.getInt("page");
                    for (int i=0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    pageText.setText(Integer.toString(page));
                    Log.i(TAG, String.format("Loaded %s movies on page %s", results.length(), page));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playing endpoint", throwable, true);
            }
        });
    }

    // get the configuration from the API
    private void getConfiguration (){
        String url = API_BASE_URL + "/configuration";
        // set request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // api key always required
        // execute a get request expecting a JSON response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get values out of JSON
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configurations with imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));

                    // pass config to adapter
                    adapter.setConfig(config);
                    // get now playing movies
                    getNowPlaying();
                } catch (JSONException e) {
                    // Log error when caught to user!
                    logError("Failed parsing configuration", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    // handle errors, log and alert the user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        log.e(TAG, message, error);
        // alter the user to avoid silent error
        if (alertUser) {
            // show a toast to user
            Toast.makeText(getApplicationContext(), message,  Toast.LENGTH_LONG).show();
        }
    }

    // previous page click
    public void onPreviousPage(View view) {
        if (page > 1) {
            page--;
            movies = new ArrayList<>(); // restart movies
            adapter = new MovieAdapter(movies); // reconfigure adapter with new movies
            // resolve the recycler view and connect a layout manager and the adapter
            rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
            rvMovies.setLayoutManager(new LinearLayoutManager(this));
            rvMovies.setAdapter(adapter);
            getConfiguration(); // get conf with the new page

            Log.i(TAG, String.format("PREVIOUS PAGE maxpage: %s, current page: %s", maxPage, page));
        }
    }

    // next page click
    public void onNextPage(View view) {
        if (page < maxPage) {
            page++;
            setMainVariables(); // see function at the end
            getConfiguration(); // get conf with the new page

            Log.i(TAG, String.format("NEXT PAGE maxpage: %s, current page: %s", maxPage, page));
        }
    }

    public void setMainVariables(){
        movies = new ArrayList<>(); // restart movies OR initialize movies
        // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies); // reconfigure adapter with new movies in case of next/prev page
        // resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);
    }
}
