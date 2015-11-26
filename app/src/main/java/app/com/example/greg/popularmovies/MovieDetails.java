package app.com.example.greg.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Greg on 25-11-2015.
 */
public class MovieDetails  extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        Bundle b = getIntent().getExtras();
        Movie currentMovie = b.getParcelable("app.com.example.greg.popularmovies.Movie");
        TextView titleTextView =  (TextView)findViewById(R.id.tvTitle);
        titleTextView.setText(currentMovie.Title);
        TextView releaseDateTextView =  (TextView)findViewById(R.id.tvReleaseDate);
        releaseDateTextView.setText(currentMovie.ReleaseDate);
        TextView descriptionTextView =  (TextView)findViewById(R.id.tvDescription);
        descriptionTextView.setText(currentMovie.Overview);
        TextView voteAverageTextView =  (TextView)findViewById(R.id.tvVoteAverage);
        voteAverageTextView.setText(currentMovie.VoteAverage);
        ImageView iv = (ImageView) findViewById(R.id.posterImage);
        Picasso
                .with(this)
                .load("http://image.tmdb.org/t/p/w185/" + currentMovie.PosterPath)
                .into(iv);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
