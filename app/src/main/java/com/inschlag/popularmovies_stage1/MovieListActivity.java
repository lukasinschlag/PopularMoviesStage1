package com.inschlag.popularmovies_stage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inschlag.popularmovies_stage1.data.Movie;
import com.inschlag.popularmovies_stage1.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MovieListActivity extends AppCompatActivity {

    private static final String API_KEY = "TODO";
    private static final String MOVIE_FILTER = "movie_filter";

    private ProgressBar mProgressBar;
    private MoviesAdapter mAdapter;
    private int mSelectedFilter = Movie.MOVIES_MOST_POPULAR;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mSharedPrefs = getPreferences(MODE_PRIVATE);
        mSelectedFilter = mSharedPrefs.getInt(MOVIE_FILTER, Movie.MOVIES_MOST_POPULAR);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        recyclerView.setLayoutManager(new GridLayoutManager(this, landscape ? 3 : 2));

        mProgressBar = findViewById(R.id.progressBar);

        mAdapter = new MoviesAdapter();
        recyclerView.setAdapter(mAdapter);

        // store the movies to be reused on e.g. orientation change
        if(savedInstanceState != null && savedInstanceState.containsKey(Movie.MOVIE_KEY)){
            mAdapter.setMovies((savedInstanceState.<Movie>getParcelableArrayList(Movie.MOVIE_KEY)));
        } else {
            loadMovies(mSelectedFilter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(MovieListActivity.class.getCanonicalName(), "onSaveInstanceState");
        outState.putParcelableArrayList(Movie.MOVIE_KEY, mAdapter.getMovies());
        outState.putInt(MOVIE_FILTER, mSelectedFilter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * Store the selection as shared preference.
         * Because with only the instance state, the user selection won't be stored when the
         * activity is paused and then resumed.
         */
        mSharedPrefs.edit().putInt(MOVIE_FILTER, mSelectedFilter).apply();
    }

    /*
                 * per Implementation Guide, regarding internet access:
                 * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
                 */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadMovies(int filter) {
        if(!isOnline()){
            Toast.makeText(MovieListActivity.this, R.string.movie_err_inet, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            ArrayList<Movie> mMovies = new LoadMovies(MovieListActivity.this, filter, API_KEY).execute().get();
            if (mMovies != null && mMovies.size() > 0) {
                mAdapter.setMovies(mMovies);
            }
        } catch (InterruptedException e) {
            Log.d(MovieListActivity.class.getCanonicalName(), "Interrupted LoadMovies: " + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(MovieListActivity.class.getCanonicalName(), "Error while executing LoadMovies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movielist, menu);
        menu.findItem(mSelectedFilter == Movie.MOVIES_MOST_POPULAR ?
                R.id.menu_sort_by_most_popular : R.id.menu_sort_by_highest_rated).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_sort_by_most_popular:
                loadMovies((mSelectedFilter = Movie.MOVIES_MOST_POPULAR));
                return true;
            case R.id.menu_sort_by_highest_rated:
                loadMovies((mSelectedFilter = Movie.MOVIES_HIGHEST_RATED));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

        private ArrayList<Movie> movies = new ArrayList<>();

        class MovieViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            TextView title;

            MovieViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.movie_img);
                title = itemView.findViewById(R.id.movie_title);
            }
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View movieView = inflater.inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(movieView);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            Movie movie = movies.get(position);
            Picasso.with(MovieListActivity.this).load(movie.getImg()).into(holder.img);

            final Bundle movieDetailArgs = new Bundle();
            movieDetailArgs.putParcelable(Movie.MOVIE_KEY, movie);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailActivity = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                    detailActivity.putExtras(movieDetailArgs);
                    startActivity(detailActivity);
                }
            });
            holder.title.setText(movie.getTitle());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        void setMovies(ArrayList<Movie> movies) {
            this.movies = movies;
            notifyDataSetChanged();
        }

        ArrayList<Movie> getMovies(){
            return movies;
        }
    }

    private void setProgressState(boolean active){
        mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    /**
     * Resource: https://developer.android.com/reference/android/os/AsyncTask.html
     * note: static to prevent leaks
     */
    static class LoadMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private String mUrl;
        private WeakReference<MovieListActivity> activityReference;

        LoadMovies(MovieListActivity context, int rating, String apiKey){
            activityReference = new WeakReference<>(context);
            switch (rating){
                case Movie.MOVIES_MOST_POPULAR:
                    mUrl = Movie.REQUEST_MOST_POPULAR;
                    break;
                case Movie.MOVIES_HIGHEST_RATED:
                    mUrl = Movie.REQUEST_HIGHEST_RATED;
                    break;
            }
            mUrl = mUrl.concat(apiKey);
        }

        /*
         * Activate\Deactivate the ProgressBar
         */
        private void postState(boolean active){
            MovieListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.setProgressState(active);
        }

        @Override
        protected void onPreExecute() {
            postState(true);
        }

        /*
         * Resources:
         * https://developer.android.com/reference/java/net/HttpURLConnection.html
         * https://docs.oracle.com/javase/tutorial/essential/regex/bounds.html
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            ArrayList<Movie> movies = null;
            InputStream in = null;
            try {
                URL url = new URL(mUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());

                Scanner scanner = new Scanner(in).useDelimiter("\\A");

                if(scanner.hasNext()) {
                    movies = JsonUtils.parseMovieJson(scanner.next());
                }
            } catch (MalformedURLException e) {
                Log.d(LoadMovies.class.getCanonicalName(), "Error while parsing url: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(LoadMovies.class.getCanonicalName(), "Error while opening connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(LoadMovies.class.getCanonicalName(), "Error while closing InputStream: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            postState(false);
        }
    }
}
