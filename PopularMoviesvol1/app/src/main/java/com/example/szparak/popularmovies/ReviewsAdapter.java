package com.example.szparak.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Szparak on 26.04.2017.
 */

public class ReviewsAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    String[] reviewerNames , reviews;
    TextView reviewerName, review;


    public ReviewsAdapter(@NonNull Context context, String[] reviewerNames, String[] reviews) {
        super(context, R.layout.review_layout, reviewerNames);

        this.reviews = reviews;
        this.reviewerNames = reviewerNames;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.review_layout, parent, false);
        }



        reviewerName = (TextView) convertView.findViewById(R.id.reviewer);
        review = (TextView) convertView.findViewById(R.id.review);
        reviewerName.setText(reviewerNames[position]);
        review.setText(reviews[position]);
        return convertView;
    }
}
