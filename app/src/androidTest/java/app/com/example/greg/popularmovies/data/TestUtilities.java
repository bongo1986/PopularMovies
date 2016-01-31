package app.com.example.greg.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Greg on 08-01-2016.
 */
public class TestUtilities extends AndroidTestCase {
    static ContentValues[] createReviewValues(long movieRowId){
        ArrayList<ContentValues> reviews = new ArrayList<ContentValues>();
        for(int i = 0; i < 5; i++){
            ContentValues values = new ContentValues();
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_CONTENT, "bla bla");
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_REVIEW_ID, "1234");
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
            values.put(PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR, "author" + String.valueOf(i));
            reviews.add(values);
        }
        ContentValues[] arr = new ContentValues[reviews.size()];
        return reviews.toArray(arr);
    }
    static ContentValues[] createTrailerValues(long movieRowId){
        ArrayList<ContentValues> reviews = new ArrayList<ContentValues>();
        for(int i = 0; i < 5; i++){
            ContentValues values = new ContentValues();
            values.put(PopularMoviesContract.TrailerEntry.COLUMN_KEY, "aaaaTestKey");
            values.put(PopularMoviesContract.TrailerEntry.COLUMN_NAME, "testName");
            values.put(PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
            reviews.add(values);
        }
        ContentValues[] arr = new ContentValues[reviews.size()];
        return reviews.toArray(arr);
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW, "overview of the movie");
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH, "");
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01-01-01");
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_TITLE, "Test title");
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 5.1);
        movieValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID, 3);
        return movieValues;
    }
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue.toString(), valueCursor.getString(idx));
        }
    }
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
