package com.example.noran.mymovies;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;



public class GridViewAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Movie> mGridData = new ArrayList<Movie>();


    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<Movie> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }



    public void setGridData(ArrayList<Movie> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mGridData.size();
    }
    @Override
    public Movie getItem(int position){
        return mGridData.get(position);
    }
      @Override
     public long getItemId(int position){

    return position;
}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movie item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));


        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+mGridData.get(position).getImage()).into(holder.imageView);

        return row;
    }

    public static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}