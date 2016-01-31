package app.com.example.greg.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Greg on 10-01-2016.
 */
public class PopularMoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mPopularMoviesHelper;
    private static final int FAVORITE_MOVIES = 100;
    private static final int FAVORITE_MOVIE_BY_ID = 200;

    private static final int REVIEWS = 300;
    private static final int TRAILERS = 400;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopularMoviesContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        matcher.addURI(authority, PopularMoviesContract.PATH_FAVORITE_MOVIES + "/*", FAVORITE_MOVIE_BY_ID);

        matcher.addURI(authority, PopularMoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, PopularMoviesContract.PATH_TRAILERS, TRAILERS);

        return matcher;
    }

    private static final String sFavoriteMovieSelection =
            PopularMoviesContract.MovieEntry.TABLE_NAME+
                    "." + PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    @Override
    public boolean onCreate() {
        mPopularMoviesHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case FAVORITE_MOVIES:
            {
               retCursor = mPopularMoviesHelper.getReadableDatabase().query(
                        PopularMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS:
            {
                retCursor = mPopularMoviesHelper.getReadableDatabase().query(
                        PopularMoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS:
            {
                retCursor = mPopularMoviesHelper.getReadableDatabase().query(
                        PopularMoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE_MOVIE_BY_ID:
            {
                retCursor = getFavoriteMovieById(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }



    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FAVORITE_MOVIES:
                return PopularMoviesContract.MovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE_BY_ID:
                return  PopularMoviesContract.MovieEntry.CONTENT_ITEM_TYPE ;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mPopularMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE_MOVIES: {
                long _id = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopularMoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPopularMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FAVORITE_MOVIES:
                rowsDeleted = db.delete(
                        PopularMoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        PopularMoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        PopularMoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private Cursor getFavoriteMovieById(Uri uri, String[] projection, String sortOrder) {
        int id = PopularMoviesContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{Integer.toString(id)};
        selection = sFavoriteMovieSelection;


        return mPopularMoviesHelper.getReadableDatabase().query(
                PopularMoviesContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mPopularMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case REVIEWS:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PopularMoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PopularMoviesContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
