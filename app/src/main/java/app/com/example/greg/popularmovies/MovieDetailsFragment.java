package app.com.example.greg.popularmovies;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import app.com.example.greg.popularmovies.data.PopularMoviesContract;

/**
 * Created by Greg on 30-01-2016.
 */
public class MovieDetailsFragment extends Fragment  {

    private Button btnAddToFavorites;
    private ScrollView dContainer;
    private ListView lvReviews;
    private ListView lvTrailers;
    private TextView tvLoadingTrailers;
    private TextView tvLoadingReviews;
    private Movie currentMovie;
    private ImageView iv;
    private View inflatedView;
    private ArrayAdapter<Review> reviewsAdapter;
    private ArrayList<Review> reviews = new ArrayList<Review>();
    private ArrayList<MovieTrailer> trailers = new ArrayList<MovieTrailer>();


    public void MovieSelected(Movie m) {
        currentMovie = m;
        m.Reviews = reviews;
        m.MovieTrailers = trailers;
        TextView titleTextView =  (TextView)inflatedView.findViewById(R.id.tvTitle);
        titleTextView.setText(currentMovie.Title);
        TextView releaseDateTextView =  (TextView)inflatedView.findViewById(R.id.tvReleaseDate);
        lvReviews = (ListView)inflatedView.findViewById(R.id.reviewsListView);
        tvLoadingReviews = (TextView )inflatedView.findViewById(R.id.loadingReviewsTextView);
        lvTrailers = (ListView)inflatedView.findViewById(R.id.trailersListView);
        tvLoadingTrailers = (TextView) inflatedView.findViewById(R.id.loadingTrailersTextView);
        releaseDateTextView.setText(currentMovie.ReleaseDate);
        TextView descriptionTextView =  (TextView)inflatedView.findViewById(R.id.tvDescription);
        descriptionTextView.setText(currentMovie.Overview);
        TextView voteAverageTextView =  (TextView)inflatedView.findViewById(R.id.tvVoteAverage);
        voteAverageTextView.setText(currentMovie.VoteAverage);
        btnAddToFavorites = (Button)inflatedView.findViewById(R.id.btnAddToFavorites);

        iv = (ImageView) inflatedView.findViewById(R.id.posterImage);

        MovieTrailerAdapter mtAdapter = new MovieTrailerAdapter(getView().getContext(),trailers);
        lvTrailers.setAdapter(mtAdapter);
        DownloadTrailerLinksTask trailersTask = new DownloadTrailerLinksTask(getView().getContext(),trailers,currentMovie.Id, currentMovie.dbId,tvLoadingTrailers,lvTrailers,mtAdapter,currentMovie.PosterBytes.length > 0);
        trailersTask.execute();

        MovieReviewsTask reviewsTask;
        reviewsAdapter = new ArrayAdapter<Review>(getView().getContext(), R.layout.review_item, R.id.tvReview,  reviews);
        lvReviews.setAdapter(reviewsAdapter);
        if(currentMovie.PosterBytes.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentMovie.PosterBytes, 0, currentMovie.PosterBytes.length);
            iv.setImageBitmap(bitmap);
            reviewsTask = new MovieReviewsTask(tvLoadingReviews,lvReviews, reviewsAdapter, reviews, getView().getContext(), true, currentMovie.Id, currentMovie.dbId );

        }
        else {
            Picasso
                    .with(getView().getContext())
                    .load("http://image.tmdb.org/t/p/w185/" + currentMovie.PosterPath)
                    .into(iv);
            reviewsTask = new MovieReviewsTask(tvLoadingReviews,lvReviews, reviewsAdapter, reviews, getView().getContext(), false, currentMovie.Id , currentMovie.dbId);
        }
        Button btn = (Button) inflatedView.findViewById(R.id.btnAddToFavorites);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovieToFavorites();

            }
        });
        reviewsTask.execute();

        hideFavouritesBtn();
        dContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.movie_details_content, container, false);
        dContainer = (ScrollView)inflatedView.findViewById(R.id.movieDetailsContainer);
        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addMovieToFavorites() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW, currentMovie.Overview);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH, currentMovie.PosterPath);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, currentMovie.ReleaseDate);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_TITLE, currentMovie.Title);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, currentMovie.VoteAverage);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID, currentMovie.Id);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER, getPosterBytes());

        Uri movieRowUri = getView().getContext().getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, movieValues);
        insertReviewsForMovie(movieRowUri);
        insertTrailersForMovie(movieRowUri);
        Toast.makeText(getView().getContext(), getString(R.string.added_to_favorites_msg),
                Toast.LENGTH_LONG).show();
        btnAddToFavorites.setVisibility(View.INVISIBLE);
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
        getView().getContext().getContentResolver().bulkInsert(PopularMoviesContract.ReviewEntry.CONTENT_URI, reviewVals.toArray(arr));

    }
    private void insertTrailersForMovie(Uri movieRowUri){
        long movieRowId = ContentUris.parseId(movieRowUri);
        ArrayList<ContentValues> trailerVals = new ArrayList<ContentValues>();
        for(int i = 0; i < trailers.size(); i++){
            ContentValues values = new ContentValues();
            MovieTrailer currentT = trailers.get(i);
            values.put(PopularMoviesContract.TrailerEntry.COLUMN_NAME, currentT.Name);
            values.put(PopularMoviesContract.TrailerEntry.COLUMN_KEY, currentT.Key);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
            trailerVals.add(values);
        }
        ContentValues[] arr = new ContentValues[trailerVals.size()];
        getView().getContext().getContentResolver().bulkInsert(PopularMoviesContract.TrailerEntry.CONTENT_URI, trailerVals.toArray(arr));

    }
    private byte[] getPosterBytes(){
        Bitmap bmp = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    private void hideFavouritesBtn(){
        Cursor cursor = getView().getContext().getContentResolver().query(
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
