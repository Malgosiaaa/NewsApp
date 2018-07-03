package com.example.malgosia.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.article_item_view, parent, false);
        }
        // Current position on the list
        Article currentArticle = getItem(position);

        // Find the TextView with title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentArticle.getmTitle());

        // Find the TextView with author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentArticle.getmAuthor());

        // Find date TextView
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(currentArticle.getmDate());


        // Find TextView with section
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section);
        sectionTextView.setText(currentArticle.getmSection());

        return listItemView;
    }
}
