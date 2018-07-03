package com.example.malgosia.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.malgosia.newsapp.MainActivity.LOG_TAG;

// Helper methods used to receiving data from The Guardian API
public final class QueryUtils {

    // private constructor:
    // this class should hold the static variables and methods accessed only from this class
    // (without creating an object of this class)

    private QueryUtils() {

    }

    // Return the URL object from the given URL String
    private static URL createUrl(String originalUrl) {
        URL url = null;
        try {
            url = new URL(originalUrl);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Url weren't correctly build ", e);
        }
        return url;
    }

    // Format date given in JSON response
    private static String formatDate(String originalDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedDate = jsonFormatter.parse(originalDate);
            String finalDate = "dd-MMM-yyyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDate, Locale.US);
            return finalDateFormatter.format(parsedDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error while parsing date from JSON response: ", e);
            return "";
        }
    }

    // Make HTTP request with the given Guardian API link
    // Return a response in String
    private static String HttpRequest(URL url) throws IOException {
        String jsonFormat = "";

        // if there are no data return
        if (url == null) {
            return jsonFormat;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(1000);
            httpURLConnection.setConnectTimeout(1500);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // if we get the response code 200 which means that request has been successful
            // read from input stream and parse the given response
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonFormat = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "The response code is: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem while retrieving articles JSON response", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                // while closing the inputStream IOException might be thrown
                inputStream.close();
            }
        }
        return jsonFormat;
    }

    //convert the whole JSON response into String
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // Extract data from JSON response to return the List of Article objects
    private static List<Article> extractJson(String articleJSON) {
        // if the String with JSON response is empty return now
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }
        // Create the ArrayList which now is empty but will be used to add articles
        List<Article> articles = new ArrayList<>();

        // Try to parse String with JSON response.
        // Catch the JSONException, if it will be thrown to prevent app crash
        try {
            // Create JSON Object from the String with JSON response
            JSONObject jsonResponse = new JSONObject(articleJSON);

            // Extract the JSONArray under the key "response"
            // which is a list of all articles data
            JSONObject articlesObject = jsonResponse.getJSONObject("response");
            // Extract articles properties from the Articles Array
            JSONArray articlesArray = articlesObject.getJSONArray("results");

            // Using for loop create an article object for every item in articlesArray
            for (int i = 0; i < articlesArray.length(); i++) {
                // Get the article at current position i at the list
                JSONObject currentArticle = articlesArray.getJSONObject(i);

                // Extract the String with title and author if available
                String fullTitle = currentArticle.optString("webTitle");

                // Extract the String with article section
                String section = currentArticle.optString("pillarName");

                // Extract the String with publication date
                String date = currentArticle.optString("webPublicationDate");
                date = formatDate(date);

                // Extract the String with the publication url
                String url = currentArticle.optString("webUrl");

                // Extract properties from Tags Array in which information about author is stored
                JSONArray tagsArray = currentArticle.getJSONArray("tags");

                // Initialize String variable author
                String author = "";

                // Check if the tags Array is empty, otherwise get the info about author
                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject tagObject = tagsArray.getJSONObject(j);
                        author += tagObject.optString("webTitle") + ". ";
                    }
                }

                // Create the new article object which includes title, author, section, date and url
                Article article = new Article(fullTitle, author, section, date, url);

                // Add that object to articles List
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error JSONException is thrown while executing any of tasks in the "try" section,
            // catch the exception, so the app won't crash.
            Log.e("QueryUtils", "Problem while parsing the articles JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    // Query the Guardian dataset and return the list of Article objects
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create the url object
        URL url = createUrl(requestUrl);

        // Create HTTP request to the URL and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = HttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem while performing the HTTP request.", e);
        }

        // Extract appropriate fields from JSON response and create articles list
        List<Article> articles = extractJson(jsonResponse);
        // Return the list of articles
        return articles;
    }
}
