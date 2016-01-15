package app.com.example.greg.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
//http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=f135d7e7a8fb0ec7757abb6917f01fbb

    private View loadingMoviesTextView;
    private GridView moviesGrid;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadingMoviesTextView = findViewById(R.id.loadingMoviesList);
        moviesGrid = (GridView)findViewById(R.id.moviesGridView);
        movies = new ArrayList<Movie>();
        moviesAdapter = new MoviesAdapter(this, movies);
        moviesGrid.setAdapter(moviesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortCriteria = sharedPref.getString(getString(R.string.pref_sortOrderTypeKey), "");

        DownloadMovieListTask task = new DownloadMovieListTask(loadingMoviesTextView, moviesGrid, moviesAdapter, movies, sortCriteria, this);
        task.execute();

    }
}
