package app.com.example.greg.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Greg on 17-11-2015.
 */
public class Movie implements Parcelable {
    public String Title;
    public String Overview;
    public String PosterPath;
    public String ReleaseDate;
    public String VoteAverage;

    public Movie(String title, String overview, String posterPath, String releaseDate, String voteAverage){
        Title = title;
        Overview = overview;
        PosterPath = posterPath;
        ReleaseDate = releaseDate;
        VoteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        Title = in.readString();
        Overview = in.readString();
        PosterPath = in.readString();
        ReleaseDate = in.readString();
        VoteAverage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(Overview);
        dest.writeString(PosterPath);
        dest.writeString(ReleaseDate);
        dest.writeString(VoteAverage);
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
