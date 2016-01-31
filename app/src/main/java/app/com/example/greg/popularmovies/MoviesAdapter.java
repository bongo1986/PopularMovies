package app.com.example.greg.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Greg on 17-11-2015.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private ArrayList<Movie> mGridData;
    private OnMovieSelectedListener mOnMovieSelectedListener;

    public MoviesAdapter(Context context, ArrayList<Movie> movieList,  OnMovieSelectedListener litener) {
        super(context, 0, movieList);
        mGridData = movieList;
        mContext = context;
        mOnMovieSelectedListener = litener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MovieImageViewHolder holder;
        Movie m =  getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_cell, parent, false);
            ImageView iv = (ImageView) row.findViewById(R.id.gridCellImageView);
            iv.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    MovieImageViewHolder currentHolder = (MovieImageViewHolder)v.getTag();
                    mOnMovieSelectedListener.MovieSelected(currentHolder.CurrentMovie);
                }
            });
            holder = new MovieImageViewHolder(iv, m);
            row.setTag(holder);
        } else {
            holder = (MovieImageViewHolder) row.getTag();
        }
        holder.CurrentMovie = m;
        if(m.PosterBytes != null && m.PosterBytes.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(m.PosterBytes, 0, m.PosterBytes.length);
            holder.CurrentImageView.setImageBitmap(bitmap);
        }
        else {
            Picasso
                    .with(mContext)
                    .load("http://image.tmdb.org/t/p/w185/" + m.PosterPath)
                    .into(holder.CurrentImageView);
        }
        return row;
    }
}
