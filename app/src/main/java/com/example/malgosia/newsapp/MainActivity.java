package com.example.malgosia.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {

    /**
     * URL with the articles data from the Guardian data set
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?from-date=2018-01-01&order-by=newest&show-tags=contributor&q=poland&api-key=87b24211-4db9-40f4-8bcf-b34b71dbdd39";

    /**
     * Constant value for the article loader ID
     */
    private static final int ARTICLE_LOADER_ID = 1;

    /**
     * Adapter for the articles list
     */
    private ArticleAdapter mAdapter;

    /**
     * TextView which will be shown when the list will be empty
     */
    private TextView mEmptyTextView;

    public static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView by ID
        ListView articleListView = (ListView) findViewById(R.id.list_view);

        mEmptyTextView = (TextView) findViewById(R.id.no_items_view);
        articleListView.setEmptyView(mEmptyTextView);

        // Create new ArticleAdapter
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the ListView which will be populated
        articleListView.setAdapter(mAdapter);

        // Set OnItemClickListener in purpose to open article in browser
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the article which was clicked
                Article currentArticle = mAdapter.getItem(position);

                // Convert the URL into Uri object
                Uri articleUri = Uri.parse(currentArticle.getmUrl());
                // Create intent to open browser with that url
                Intent webIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(webIntent);
            }
        });

        // Check the state of network connection by connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get information on currently active network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // if the network connection is active fetch the data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get the LoaderManager to interact with loader
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the LoaderManager
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            // Display message with info about no internet connection after hiding progress bar
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            mEmptyTextView.setText(R.string.no_inet);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the Guardian URL
        return new ArticleLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide progress bar when the data have been loaded
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // Set empty state in case of no articles found
        mEmptyTextView.setText(getString(R.string.no_articles));
        // Clear the adapter from previous articles
        mAdapter.clear();

        // Update the List if there are valid data
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Clear the present data
        mAdapter.clear();
    }
}
