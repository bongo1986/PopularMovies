package app.com.example.greg.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Greg on 17-11-2015.
 */
public class Movie implements Parcelable {
    public String Title;
    public String Overview;
    public String PosterPath;
    public String ReleaseDate;
    public String VoteAverage;
    public byte[] PosterBytes;
    public ArrayList<MovieTrailer> MovieTrailers;
    public ArrayList<Review> Reviews;
    public int Id;
    public int dbId;


    public Movie(int id, String title, String overview, String posterPath, String releaseDate, String voteAverage, byte[] posterBytes, int dId){
        Id = id;
        Title = title;
        Overview = overview;
        PosterPath = posterPath;
        ReleaseDate = releaseDate;
        VoteAverage = voteAverage;
        PosterBytes = posterBytes;
        dbId = dId;
    }
    public Movie(int id, String title, String overview, String posterPath, String releaseDate, String voteAverage){
        Id = id;
        Title = title;
        Overview = overview;
        PosterPath = posterPath;
        ReleaseDate = releaseDate;
        VoteAverage = voteAverage;
        PosterBytes = new byte[0];
    }

    protected Movie(Parcel in) {
        Title = in.readString();
        Overview = in.readString();
        PosterPath = in.readString();
        ReleaseDate = in.readString();
        VoteAverage = in.readString();
        Id = in.readInt();
        dbId = in.readInt();
        PosterBytes = new byte[in.readInt()];
        in.readByteArray(PosterBytes);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(Overview);
        dest.writeString(PosterPath);
        dest.writeString(ReleaseDate);
        dest.writeString(VoteAverage);
        dest.writeInt(Id);
        dest.writeInt(dbId);
        dest.writeInt(PosterBytes.length);
        dest.writeByteArray(PosterBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
