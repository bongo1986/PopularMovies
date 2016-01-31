package app.com.example.greg.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Greg on 28-01-2016.
 */
public class MovieGridFragment  extends Fragment {
    private View loadingMoviesTextView;
    private GridView moviesGrid;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar)  getView().findViewById(R.id.toolbar);
        View v = inflater.inflate(R.layout.content_main, container, false);
        loadingMoviesTextView = v.findViewById(R.id.loadingMoviesList);

        moviesGrid = (GridView)v.findViewById(R.id.moviesGridView);
        movies = new ArrayList<Movie>();

        moviesAdapter = new MoviesAdapter(v.getContext(), movies, (OnMovieSelectedListener)getActivity());
        moviesGrid.setAdapter(moviesAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMovies();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getView().getContext(), AppPreferences.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        String sortCriteria = sharedPref.getString(getString(R.string.pref_sortOrderTypeKey), "");

        DownloadMovieListTask task = new DownloadMovieListTask(loadingMoviesTextView, moviesGrid, moviesAdapter, movies, sortCriteria, getView().getContext());
        task.execute();

    }
}
