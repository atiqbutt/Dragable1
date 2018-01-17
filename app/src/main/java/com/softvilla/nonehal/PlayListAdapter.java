package com.softvilla.nonehal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hassan on 10/28/2017.
 */

public class PlayListAdapter extends ArrayAdapter<videoInfo>{
    Context context;
    ArrayList<videoInfo> data;
    ArrayList<videoInfo> searchData;
    LayoutInflater inflater;

    public PlayListAdapter(Context context,ArrayList<videoInfo> data) {
        super(context, 0,data);

        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.yt_row, parent, false);
        }


        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.image);

        TextView name = (TextView) convertView.findViewById(R.id.title);

       // RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative);

        Picasso.with(context).load(data.get(position).thumbnail).placeholder(R.drawable.placeholder).fit().centerCrop().into(thumbNail);
        name.setText(data.get(position).name);

        /*relativeLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, DragableTest.class).putExtra("id",data.get(position).videoId));
                    }
                }
        );*/

        return convertView;

    }


    /*@Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                searchData = (ArrayList<videoInfo>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<videoInfo> FilteredArrList = new ArrayList<videoInfo>();

                if (data == null) {
                    data = new ArrayList<videoInfo>(searchData); // saves the original data in mOriginalValues
                }

                *//********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********//*
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = data.size();
                    results.values = data;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < data.size(); i++) {
                       //String data1 = data.get(i).thumbnail;
                        String data2 = data.get(i).name;
                        if (data2.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new videoInfo());
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }*/
}

