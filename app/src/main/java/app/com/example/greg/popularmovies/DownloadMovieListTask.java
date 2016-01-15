package app.com.example.greg.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.com.example.greg.popularmovies.data.PopularMoviesContract;

/**
 * Created by Greg on 18-11-2015.
 */
public class DownloadMovieListTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

    private View loadingMoviesTextView;
    private View moviesGrid;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movies;
    private String sortCriteria;
    private Context mContext;
    private Boolean loadFromDb;

    DownloadMovieListTask(View loadingMoviesTextView, View moviesGrid, MoviesAdapter moviesAdapter,    ArrayList<Movie> movies, String sortCriteria, Context context){
        this.loadingMoviesTextView = loadingMoviesTextView;
        this.moviesGrid = moviesGrid;
        this.moviesAdapter = moviesAdapter;
        this.movies = movies;
        this.sortCriteria = sortCriteria;
        this.mContext = context;
        this.loadFromDb = sortCriteria.equals("favorites");
    }
    
    
    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {
        if(this.loadFromDb == true){
            loadMoviesFromDb();
        }else {
            loadMoviesFromInternet();
        }
        return movies;
    }
    @Override
    protected void onPostExecute(ArrayList<Movie> result){
        loadingMoviesTextView.setVisibility(View.GONE);
        moviesGrid.setVisibility(View.VISIBLE);
        moviesAdapter.notifyDataSetChanged();
    }
    private void loadMoviesFromDb() {
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        movies.clear();
        while (cursor.isAfterLast() == false){
            int id = cursor.getInt(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_TITLE));
            String overview = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW));
            String posterPath = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
            String voteAverage = String.valueOf(cursor.getFloat(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            byte[] posterBytes = cursor.getBlob(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_POSTER));
            int dbId = cursor.getInt(cursor.getColumnIndex(PopularMoviesContract.MovieEntry._ID));
            Movie mov = new Movie(id, title,overview,posterPath, releaseDate, voteAverage, posterBytes, dbId);
            movies.add(mov);
            cursor.moveToNext();
        }
        cursor.close();
    }
    private ArrayList<Movie> loadMoviesFromInternet(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        try {

            final String MOVIES_BASE_URL = mContext.getString(R.string.api_address);
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sortCriteria)
                    .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {

                return null;
            }
            forecastJsonStr = buffer.toString();
            extractMovieData(forecastJsonStr);
            return movies;
        } catch (IOException e) {

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

    }
    private void extractMovieData(String json){
        movies.clear();
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("results");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    int id = oneObject.getInt("id");
                    String title = oneObject.getString("original_title");
                    String overview = oneObject.getString("overview");
                    String posterPath = oneObject.getString("poster_path");
                    String releaseDate = oneObject.getString("release_date");
                    String voteAverage = oneObject.getString("vote_average");

                    Movie mov = new Movie(id, title,overview,posterPath, releaseDate, voteAverage);
                    movies.add(mov);
                } catch (JSONException e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
