package app.com.example.greg.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import app.com.example.greg.popularmovies.data.PopularMoviesContract;

/**
 * Created by Greg on 25-11-2015.
 */
public class MovieDetails  extends AppCompatActivity  {

    private Button btnAddToFavorites;
    private ListView lvReviews;
    private TextView tvLoadingReviews;
    private Movie currentMovie;
    private ImageView iv;
    private ArrayAdapter<Review> reviewsAdapter;
    private ArrayList<Review> reviews = new ArrayList<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        Bundle b = getIntent().getExtras();
        currentMovie = b.getParcelable("app.com.example.greg.popularmovies.MovieEntry");
        TextView titleTextView =  (TextView)findViewById(R.id.tvTitle);
        titleTextView.setText(currentMovie.Title);
        TextView releaseDateTextView =  (TextView)findViewById(R.id.tvReleaseDate);
        lvReviews = (ListView)findViewById(R.id.reviewsListView);
        tvLoadingReviews = (TextView )findViewById(R.id.loadingReviewsTextView);
        releaseDateTextView.setText(currentMovie.ReleaseDate);
        TextView descriptionTextView =  (TextView)findViewById(R.id.tvDescription);
        descriptionTextView.setText(currentMovie.Overview);
        TextView voteAverageTextView =  (TextView)findViewById(R.id.tvVoteAverage);
        voteAverageTextView.setText(currentMovie.VoteAverage);
        btnAddToFavorites = (Button)findViewById(R.id.btnAddToFavorites );

        lvReviews.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        iv = (ImageView) findViewById(R.id.posterImage);
        MovieReviewsTask reviewsTask;

        reviewsAdapter = new ArrayAdapter<Review>(this, R.layout.review_item, R.id.tvReview,  reviews);
        lvReviews.setAdapter(reviewsAdapter);
        if(currentMovie.PosterBytes.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentMovie.PosterBytes, 0, currentMovie.PosterBytes.length);
            iv.setImageBitmap(bitmap);
            reviewsTask = new MovieReviewsTask(tvLoadingReviews,lvReviews, reviewsAdapter, reviews, this, true, currentMovie.Id, currentMovie.dbId );

        }
        else {
            Picasso
                    .with(this)
                    .load("http://image.tmdb.org/t/p/w185/" + currentMovie.PosterPath)
                    .into(iv);
            reviewsTask = new MovieReviewsTask(tvLoadingReviews,lvReviews, reviewsAdapter, reviews, this, false, currentMovie.Id , currentMovie.dbId);
        }
        reviewsTask.execute();

        hideFavouritesBtn();


    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addMovieToFavorites(View view) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW, currentMovie.Overview);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH, currentMovie.PosterPath);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, currentMovie.ReleaseDate);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_TITLE, currentMovie.Title);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, currentMovie.VoteAverage);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID, currentMovie.Id);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER, getPosterBytes());

        Uri movieRowUri = this.getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, movieValues);
        insertReviewsForMovie(movieRowUri);
        Toast.makeText(this, getString(R.string.added_to_favorites_msg),
                Toast.LENGTH_LONG).show();
        btnAddToFavorites.setVisibility(View.INVISIBLE);
    }
    public void playTrailer(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
    }
    private void insertReviewsForMovie(Uri movieRowUri){
        long movieRowId = ContentUris.parseId(movieRowUri);
        ArrayList<ContentValues> reviewVals = new ArrayList<ContentValues>();
        for(int i = 0; i < reviews.size(); i++){
            ContentValues values = new ContentValues();
            Review currentR = reviews.get(i);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_CONTENT, currentR.ReviewContent);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_REVIEW_ID, currentR.ReviewId);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR, currentR.Author);
            reviewVals.add(values);
        }
        ContentValues[] arr = new ContentValues[reviewVals.size()];
        this.getContentResolver().bulkInsert(PopularMoviesContract.ReviewEntry.CONTENT_URI, reviewVals.toArray(arr));

    }
    private byte[] getPosterBytes(){
        Bitmap bmp = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    private void hideFavouritesBtn(){
        Cursor cursor = this.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + currentMovie.Id,
                null,
                null
        );
        int count = cursor.getCount();
        if(count > 0) {
            btnAddToFavorites.setVisibility(View.INVISIBLE);
        }
    }
}
