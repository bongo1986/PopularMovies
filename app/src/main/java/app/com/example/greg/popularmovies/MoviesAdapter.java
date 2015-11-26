package app.com.example.greg.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    public MoviesAdapter(Context context, ArrayList<Movie> movieList) {
        super(context, 0, movieList);
        mGridData = movieList;
        mContext = context;
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
                    Intent i = new Intent(mContext, MovieDetails.class);
                    MovieImageViewHolder currentHolder = (MovieImageViewHolder)v.getTag();
                    i.putExtra("app.com.example.greg.popularmovies.Movie", currentHolder.CurrentMovie);
                    mContext.startActivity(i);
                }
            });
            holder = new MovieImageViewHolder(iv, m);
            row.setTag(holder);
        } else {
            holder = (MovieImageViewHolder) row.getTag();
        }
        holder.CurrentMovie = m;
        Picasso
                .with(mContext)
                .load("http://image.tmdb.org/t/p/w185/" + m.PosterPath)
                .into(holder.CurrentImageView);
        return row;
    }
}
