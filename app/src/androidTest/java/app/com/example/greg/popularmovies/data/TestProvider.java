package app.com.example.greg.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Greg on 10-01-2016.
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                PopularMoviesContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );


        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Cursor cursor2 = mContext.getContentResolver().query(
                PopularMoviesContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Favorite Movies table during delete", 0, cursor.getCount());
        assertEquals("Error: Records not deleted from Reviews table during delete", 0, cursor2.getCount());
        cursor.close();
        cursor2.close();
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PopularMoviesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: provider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + PopularMoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, PopularMoviesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {

            assertTrue("Error: provider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
    public void testGetType() {
        // content://com.example.greg.popularmovies/favoritemovie/
        String type = mContext.getContentResolver().getType(PopularMoviesContract.MovieEntry.CONTENT_URI);
        //String type2 = mContext.getContentResolver().getType(PopularMoviesContract.MovieEntry.CONTENT_ITEM);


        assertEquals("Error: the Movie CONTENT_URI should return Movie.CONTENT_TYPE",
                PopularMoviesContract.MovieEntry.CONTENT_TYPE, type);
    }



    public void testBasicMovieQuery() {
        // insert our test records into the database
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert PopularMovie into the Database", movieRowId  != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", cursor, testValues);
    }

    public void testMovieByIdQuery() {
        // insert our test records into the database
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert PopularMovie into the Database", movieRowId  != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + 3,
                null,
                null
        );
        // Test the basic content provider query
        Cursor cursor2 = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null,
                PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + 1,
                null,
                null
        );
        int count = cursor2.getCount();

        assertTrue("Unable to Insert PopularMovie into the Database", count  == 0);
        TestUtilities.validateCursor("testBasicWeatherQuery", cursor, testValues);
    }


    public void testInsertReadProvider() {

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        ContentValues testValues = TestUtilities.createMovieValues();
        mContext.getContentResolver().registerContentObserver(PopularMoviesContract.MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);


    }
    public void testReviewBulkInsert() {
        ContentValues testValues = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, testValues);
        long movieRowId = ContentUris.parseId(movieUri);
        int insertedCount =  mContext.getContentResolver().bulkInsert(PopularMoviesContract.ReviewEntry.CONTENT_URI, TestUtilities.createReviewValues(movieRowId));
        assertEquals(insertedCount,5);
    }
}
