package app.com.example.greg.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Greg on 07-01-2016.
 */
public class TestDb extends AndroidTestCase {

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();

        tableNameHashSet.add(PopularMoviesContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);

        SQLiteDatabase db = new PopularMoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without movie table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PopularMoviesContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry._ID);
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable(){
        SQLiteDatabase db = new PopularMoviesDbHelper(this.mContext).getWritableDatabase();
        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, movieValues);

        assertTrue(movieRowId != -1);
        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor movieCursor = db.query(
                PopularMoviesContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", movieCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                movieCursor, movieValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                movieCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        movieCursor.close();
        db.close();
    }
}
