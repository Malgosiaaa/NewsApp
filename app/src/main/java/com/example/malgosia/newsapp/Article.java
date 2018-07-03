package com.example.malgosia.newsapp;

public class Article {

    // Article title
    private String mTitle;

    // Article author
    private String mAuthor;

    // Article thematic section
    private String mSection;

    // Article publication date
    private String mDate;

    // Link redirecting to article
    private String mUrl;

    public Article (String Title, String Author, String Section, String Date, String Url) {
        mTitle = Title;
        mAuthor = Author;
        mSection = Section;
        mDate = Date;
        mUrl = Url;
    }

    // Get the article title
    public String getmTitle() {
        return mTitle;
    }

    // Get the article author
    public String getmAuthor() {
        return mAuthor;
    }

    // Get the article section
    public String getmSection() {
        return mSection;
    }

    // Get the article publication date
    public String getmDate() {
        return mDate;
    }

    // Get the link to publication
    public String getmUrl() {
        return mUrl;
    }
}


