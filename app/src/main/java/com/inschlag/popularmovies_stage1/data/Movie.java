package com.inschlag.popularmovies_stage1.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public static final int MOVIES_MOST_POPULAR = 0;
    public static final int MOVIES_HIGHEST_RATED = 1;
    public static final String MOVIE_KEY = "movie_key";
    public static final String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String MOVIE_BACKDROP_URL = "http://image.tmdb.org/t/p/w500/";
    public static final String REQUEST_MOST_POPULAR = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String REQUEST_HIGHEST_RATED = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String FIELD_RESULTS = "results";
    public static final String FIELD_ID = "id";
    public static final String FIELD_RATING = "vote_average";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_POSTER = "poster_path";
    public static final String FIELD_BACKDROP = "backdrop_path";
    public static final String FIELD_SYNOPSIS = "overview";
    public static final String FIELD_RELEASED = "release_date";

    private int id = 0;
    private String title;
    private String img;
    private String backdrop;
    private String plot;
    private float rating;
    private String date;

    public Movie(int id, String title, String img, String backdrop, String plot, float rating, String date) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.backdrop = backdrop;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
    }

    private Movie(Parcel source){
        this.id = source.readInt();
        this.title = source.readString();
        this.img = source.readString();
        this.backdrop = source.readString();
        this.plot = source.readString();
        this.rating = source.readFloat();
        this.date = source.readString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getPlot() {
        return plot;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", backdrop='" + backdrop + '\'' +
                ", plot='" + plot + '\'' +
                ", rating=" + rating +
                ", date='" + date + '\'' +
                '}';
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeString(this.backdrop);
        dest.writeString(this.plot);
        dest.writeFloat(this.rating);
        dest.writeString(this.date);
    }
}
