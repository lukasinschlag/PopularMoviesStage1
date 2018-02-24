package com.inschlag.popularmovies_stage1.utils;

import android.util.Log;

import com.inschlag.popularmovies_stage1.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<Movie> parseMovieJson(String json) {

        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray arr = jObject.optJSONArray(Movie.FIELD_RESULTS);

            if (arr != null) {
                //Go through the movie results
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject mObj = arr.getJSONObject(i);

                    float rating = 0;
                    String ratingS = mObj.getString(Movie.FIELD_RATING);
                    if(ratingS != null){
                        rating = Float.parseFloat(ratingS);
                    }

                    movies.add(new Movie(
                            mObj.getInt(Movie.FIELD_ID),
                            mObj.getString(Movie.FIELD_TITLE),
                            Movie.MOVIE_POSTER_URL + mObj.getString(Movie.FIELD_POSTER),
                            Movie.MOVIE_BACKDROP_URL + mObj.getString(Movie.FIELD_BACKDROP),
                            mObj.getString(Movie.FIELD_SYNOPSIS),
                            rating,
                            mObj.getString(Movie.FIELD_RELEASED)
                            ));
                }
            }
        } catch (JSONException e) {
            // Err while parsing
            Log.d(JsonUtils.class.getCanonicalName(), e.getMessage());
        }

        return movies;
    }
}
