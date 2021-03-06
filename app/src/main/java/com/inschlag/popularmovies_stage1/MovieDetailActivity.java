package com.inschlag.popularmovies_stage1;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inschlag.popularmovies_stage1.data.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar;
        if((actionBar = getSupportActionBar()) != null){
            actionBar.setBackgroundDrawable(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(Movie.MOVIE_KEY)){
            mMovie = savedInstanceState.getParcelable(Movie.MOVIE_KEY);
        } else {
            Bundle args = getIntent().getExtras();
            if (args != null && args.containsKey(Movie.MOVIE_KEY)) {
                mMovie = args.getParcelable(Movie.MOVIE_KEY);
            }
        }

        if(mMovie == null){
            Toast.makeText(MovieDetailActivity.this, R.string.movie_err, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView title = findViewById(R.id.movie_title),
                releaseDate = findViewById(R.id.movie_release_date),
                rating = findViewById(R.id.movie_rating),
                plot = findViewById(R.id.movie_plot);
        ImageView img = findViewById(R.id.movie_img);
        ImageView backdrop = findViewById(R.id.movie_backdrop);

        title.setText(mMovie.getTitle());
        releaseDate.setText(mMovie.getDate());
        rating.setText(String.format(getResources().getString(R.string.movie_rating), mMovie.getRating()));
        plot.setText(mMovie.getPlot());

        Picasso.with(MovieDetailActivity.this).load(mMovie.getImg()).into(img);
        Picasso.with(MovieDetailActivity.this).load(mMovie.getBackdrop()).into(backdrop);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Movie.MOVIE_KEY, mMovie);
        super.onSaveInstanceState(outState);
    }
}
