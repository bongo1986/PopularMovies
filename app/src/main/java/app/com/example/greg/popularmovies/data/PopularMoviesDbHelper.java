package app.com.example.greg.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Greg on 07-01-2016.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    private static final  int DATABASE_VERSION = 9;
    public static String DATABASE_NAME = "movies.db";

    public PopularMoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + PopularMoviesContract.MovieEntry.TABLE_NAME + " (" +
                PopularMoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT  NOT NULL, " +
                PopularMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                PopularMoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT  NOT NULL, " +
                PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT  NOT NULL, " +
                PopularMoviesContract.MovieEntry.COLUMN_TITLE + " TEXT  NOT NULL, " +
                PopularMoviesContract.MovieEntry.COLUMN_POSTER + " BLOB " +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + PopularMoviesContract.ReviewEntry.TABLE_NAME + " (" +
                PopularMoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                PopularMoviesContract.ReviewEntry.COLUMN_CONTENT+ " TEXT  NOT NULL, " +
                PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR + " REAL NOT NULL, " +
                PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                PopularMoviesContract.MovieEntry.TABLE_NAME + " (" + PopularMoviesContract.MovieEntry._ID + ") " +
                " );";
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + PopularMoviesContract.TrailerEntry.TABLE_NAME + " (" +
                PopularMoviesContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL," +
                PopularMoviesContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY+ " TEXT NOT NULL," +
                " FOREIGN KEY (" + PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                PopularMoviesContract.MovieEntry.TABLE_NAME + " (" + PopularMoviesContract.MovieEntry._ID + ") " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
