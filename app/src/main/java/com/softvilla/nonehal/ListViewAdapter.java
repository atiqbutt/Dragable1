package com.softvilla.nonehal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Malik on 20/10/2017.
 */

public class ListViewAdapter extends ArrayAdapter<Employee> {

    private Activity activity;

    public ListViewAdapter(Activity activity, int resource, List<Employee> employees) {
        super(activity, resource, employees);
        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_list_employee, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.employeeName.setText(getItem(position).getName());
        if (getItem(position).isGender()) {
            holder.genderIcon.setImageResource(R.drawable.finallogo);
        } else {
            holder.genderIcon.setImageResource(R.drawable.finallogo);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView employeeName;
        private ImageView genderIcon;

        public ViewHolder(View v) {
            employeeName = (TextView) v.findViewById(R.id.name);
            genderIcon = (ImageView) v.findViewById(R.id.icon);
        }
    }
}
