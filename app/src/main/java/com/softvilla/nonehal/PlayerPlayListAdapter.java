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
 * Created by Hassan on 10/29/2017.
 */

public class PlayerPlayListAdapter extends ArrayAdapter<videoInfo> {

    Context context;
    ArrayList<videoInfo> data;
    public PlayerPlayListAdapter(Context context, ArrayList<videoInfo> data) {
        super(context,0,data);

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

        return convertView;

    }
}
