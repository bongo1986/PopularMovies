package app.com.example.greg.popularmovies;

import android.widget.ImageView;

/**
 * Created by Greg on 21-11-2015.
 */
public class MovieImageViewHolder {
    public ImageView CurrentImageView;
    public Movie CurrentMovie;

    MovieImageViewHolder(ImageView iv, Movie movie){
        CurrentImageView = iv;
        CurrentMovie = movie;
    }
}
