package com.example.malgosia.newsapp;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

public class ArticleLoader extends android.content.AsyncTaskLoader<List<Article>> {

    // Tag for logs
    private static final String LOG_TAG = ArticleLoader.class.getName();

    // Url with query
    private String mUrl;

    /**
     * Constructs new ArticleLoader
     *
     * @param context of this activity
     * @param url     to load the data
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Do on the background
     */
    @Nullable
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Create the network request, parse the response, and extract a list of articles
        List<Article> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
}
