package app.com.example.greg.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

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
 * Created by Greg on 15-01-2016.
 */

//http://api.themoviedb.org/3/movie/281957/videos?api_key=f135d7e7a8fb0ec7757abb6917f01fbb
public class DownloadTrailerLinksTask  extends AsyncTask<Void, Void, ArrayList<MovieTrailer>> {

    private Context mContext;
    private ArrayList<MovieTrailer> movieTrailers;
    private int movieId;
    private View loadingTrailersLinksTextView;
    private View trailersListView;
    private ArrayAdapter<MovieTrailer> trailersAdapter;
    private Boolean loadFromDb;
    private int mDbId;

    public DownloadTrailerLinksTask(Context c, ArrayList<MovieTrailer> mTrailers, int mId, int dbId, View loadingTrailersLinksTv, View trailersLv, ArrayAdapter<MovieTrailer> tAdapter, Boolean lFromDb){
        mContext = c;
        movieTrailers = mTrailers;
        movieId = mId;
        loadingTrailersLinksTextView = loadingTrailersLinksTv;
        trailersListView = trailersLv;
        trailersAdapter = tAdapter;
        loadFromDb = lFromDb;
        mDbId = dbId;
    }
    private ArrayList<MovieTrailer> loadReviewFromDb() {
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.TrailerEntry.CONTENT_URI,
                null,
                PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + " = " + mDbId,
                null,
                null
        );
        cursor.moveToFirst();
        movieTrailers.clear();
        while (cursor.isAfterLast() == false){

            String name = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.TrailerEntry.COLUMN_NAME));
            String key = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.TrailerEntry.COLUMN_KEY));

            MovieTrailer mt = new MovieTrailer(key,name);
            movieTrailers.add(mt);
            cursor.moveToNext();
        }
        cursor.close();
        return  movieTrailers;
    }
    private ArrayList<MovieTrailer> loadReviewsFromInternet(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        try {

            final String REVIEWS_BASE_URL = String.format(mContext.getString(R.string.api_trailers_address), this.movieId);
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(REVIEWS_BASE_URL).buildUpon()
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
            extractTrailerData(forecastJsonStr);
            return movieTrailers;
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
                    Log.e("popularMovies", e.getMessage());

                }
            }
        }

    }
    private void extractTrailerData(String json){
        movieTrailers.clear();
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("results");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String key = oneObject.getString("key");
                    String name = oneObject.getString("name");

                    MovieTrailer mt = new MovieTrailer(key,name);
                    movieTrailers.add(mt);
                } catch (JSONException e) {
                    Log.e("popularMovies", e.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ArrayList<MovieTrailer> doInBackground(Void... params) {
        if(loadFromDb == true){
            return loadReviewFromDb();
        }
        else {
            return loadReviewsFromInternet();
        }
    }
    @Override
    protected void onPostExecute(ArrayList<MovieTrailer> result){
        loadingTrailersLinksTextView.setVisibility(View.GONE);
        trailersListView.setVisibility(View.VISIBLE);
        trailersAdapter.notifyDataSetChanged();
    }
}
