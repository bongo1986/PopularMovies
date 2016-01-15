package app.com.example.greg.popularmovies;

/**
 * Created by Greg on 13-01-2016.
 */
public class Review {
    public String ReviewId;
    public String ReviewContent;
    public String Author;

    public Review(String rId, String rContent, String author){
        this.ReviewId = rId;
        this.ReviewContent = rContent;
        this.Author = author;
    }

    @Override
    public String toString() {
        return this.ReviewContent;
    }
}
