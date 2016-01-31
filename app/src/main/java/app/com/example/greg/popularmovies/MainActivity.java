package app.com.example.greg.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements OnMovieSelectedListener {
//http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=f135d7e7a8fb0ec7757abb6917f01fbb

    /*private View loadingMoviesTextView;
    private GridView moviesGrid;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movies;
*/
    private Movie currentMovie;
    private  MenuItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* loadingMoviesTextView = findViewById(R.id.loadingMoviesList);
        moviesGrid = (GridView)findViewById(R.id.moviesGridView);
        movies = new ArrayList<Movie>();
        moviesAdapter = new MoviesAdapter(this, movies);
        moviesGrid.setAdapter(moviesAdapter);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MovieDetailsFragment displayFrag = (MovieDetailsFragment) getFragmentManager()
                .findFragmentById(R.id.movieDetailsFragment);
        mItem = menu.findItem(R.id.action_share);
        if(displayFrag != null &&  currentMovie != null){
            showShareMenuBtn();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AppPreferences.class));
            return true;
        }
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
    public void MovieSelected(Movie m) {
        MovieDetailsFragment displayFrag = (MovieDetailsFragment) getFragmentManager()
                .findFragmentById(R.id.movieDetailsFragment);
        if (displayFrag == null) {
            Intent i = new Intent(this, MovieDetails.class);
            i.putExtra("app.com.example.greg.popularmovies.MovieEntry", m);
            this.startActivity(i);
        } else {
            showShareMenuBtn();
            currentMovie = m;
            displayFrag.MovieSelected(m);
        }
    }
    private void showShareMenuBtn(){
        mItem.setVisible(true);
    }
}
