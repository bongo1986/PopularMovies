package app.com.example.greg.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Greg on 25-01-2016.
 */
public class MovieTrailerAdapter extends ArrayAdapter<MovieTrailer> {

    private Context mContext;
    private ArrayList<MovieTrailer> mGridData;

    public MovieTrailerAdapter(Context context, ArrayList<MovieTrailer> movieList) {
        super(context, 0, movieList);
        mGridData = movieList;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MovieImageViewHolder holder;
        MovieTrailer m =  getItem(position);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        row = inflater.inflate(R.layout.movie_trailer_item, parent, false);
        TextView tv = (TextView) row.findViewById(R.id.trailerTxtTv);
        tv.setText(m.Name);
        Button btn = (Button) row.findViewById(R.id.btnPlay);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(mContext, MovieDetails.class);
                    String key = (String) v.getTag();
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +key)));


                }
            });
        btn.setTag(m.Key);
        return row;
    }
}
