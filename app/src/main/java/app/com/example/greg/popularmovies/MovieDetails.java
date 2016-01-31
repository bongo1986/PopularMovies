package app.com.example.greg.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Greg on 25-11-2015.
 */
public class MovieDetails  extends AppCompatActivity  {

    private Button btnAddToFavorites;
    private ListView lvReviews;
    private ListView lvTrailers;
    private TextView tvLoadingTrailers;
    private TextView tvLoadingReviews;
    private Movie currentMovie;
    private ImageView iv;
    private ArrayAdapter<Review> reviewsAdapter;
    private ArrayList<Review> reviews = new ArrayList<Review>();
    private ArrayList<MovieTrailer> trailers = new ArrayList<MovieTrailer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        MovieDetailsFragment movieSelectedListener =( MovieDetailsFragment) getFragmentManager()
                .findFragmentById(R.id.movieDetailsFragment);


        Bundle b = getIntent().getExtras();
        currentMovie = b.getParcelable("app.com.example.greg.popularmovies.MovieEntry");
        movieSelectedListener.MovieSelected(currentMovie);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.action_share && currentMovie.MovieTrailers != null && currentMovie.MovieTrailers.size() > 0){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + currentMovie.MovieTrailers.get(0).Key);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }





}
