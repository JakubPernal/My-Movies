package com.example.szparak.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Szparak on 25.04.2017.
 */

public class TrailersAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    String[] names;
    TextView nameText;

    public TrailersAdapter(@NonNull Context context, String[] names) {
        super(context, R.layout.trailer_layout, names);

        this.names = names;
        inflater = LayoutInflater.from(context);
        Log.v("rozmiar tablicy" , Integer.toString(names.length));

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.trailer_layout, parent, false);
        }


        nameText = (TextView) convertView.findViewById(R.id.trailer_name);
        nameText.setText(names[position]);
        return convertView;
    }
}
