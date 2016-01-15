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
 * Created by Greg on 12-01-2016.
 */

//http://api.themoviedb.org/3/movie/281957/reviews?api_key=f135d7e7a8fb0ec7757abb6917f01fbb
public class MovieReviewsTask  extends AsyncTask<Void, Void, ArrayList<Review>> {
    private View loadingReviewsTextView;
    private View reviewGrid;
    private ArrayAdapter<Review> reviewsAdapter;
    private ArrayList<Review> reviews;
    private int MovieInternetId;
    private Context mContext;
    private Boolean loadFromDb;
    private int dbId;

    MovieReviewsTask(View loadingMoviesTextView, View rGrid, ArrayAdapter<Review> adapter,    ArrayList<Review> reviews,  Context context, Boolean lFromDb, int MovieInternetId, int dbId){
        this.loadingReviewsTextView = loadingMoviesTextView;
        this.reviewGrid = rGrid;
        this.reviewsAdapter = adapter;
        this.reviews = reviews;
        this.mContext = context;
        this.loadFromDb = lFromDb;
        this.MovieInternetId = MovieInternetId;
        this.dbId = dbId;
    }
    @Override
    protected ArrayList<Review> doInBackground(Void... params) {
        if(this.loadFromDb == true){
            loadReviewFromDb();
        }else {
            loadReviewsFromInternet();
        }
        return reviews;
    }
    @Override
    protected void onPostExecute(ArrayList<Review> result){
        loadingReviewsTextView.setVisibility(View.GONE);
        reviewGrid.setVisibility(View.VISIBLE);
        reviewsAdapter.notifyDataSetChanged();
    }
    private void loadReviewFromDb() {
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.ReviewEntry.CONTENT_URI,
                null,
                PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + " = " + dbId,
                null,
                null
        );
        cursor.moveToFirst();
        reviews.clear();
        while (cursor.isAfterLast() == false){

            String id = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.ReviewEntry.COLUMN_REVIEW_ID));
            String author = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR));
            String content = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.ReviewEntry.COLUMN_CONTENT));

            Review r = new Review(id,content, author);
            reviews.add(r);
            cursor.moveToNext();
        }
        cursor.close();
    }
    private ArrayList<Review> loadReviewsFromInternet(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        try {

            final String REVIEWS_BASE_URL = String.format(mContext.getString(R.string.api_reviews_address), this.MovieInternetId);
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
            extractReviewData(forecastJsonStr);
            return reviews;
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
    private void extractReviewData(String json){
        reviews.clear();
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("results");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String id = oneObject.getString("id");
                    String author = oneObject.getString("author");
                    String content = oneObject.getString("content");

                    Review r = new Review(id,content, author);
                    reviews.add(r);
                } catch (JSONException e) {
                    Log.e("popularMovies", e.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
