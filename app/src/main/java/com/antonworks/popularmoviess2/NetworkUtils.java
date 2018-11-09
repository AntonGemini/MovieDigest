package com.antonworks.popularmoviess2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sassa_000 on 15.08.2017.
 */

public final class NetworkUtils {

    private static final String MOVIE_URL = "https://api.themoviedb.org/3/movie/";

    public static final String order_popular = "popular";
    public static final String order_top_rated = "top_rated";
    private static final String API_KEY = "api_key";
    private static final String api_key_value = "cadbea69c786274bfc280fdf921581c5";
    public static final String default_order = order_popular;


    public static URL buildUrl(String order)
    {
        Uri uri = Uri.parse(MOVIE_URL).buildUpon().appendEncodedPath(order)
                .appendQueryParameter(API_KEY,api_key_value).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException ex)
        {

        }
        return url;
    }

    public static String getResponseFromUrl(URL url)
    {
        String result = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
        }
        catch(IOException e)
        {
        }
        return result;
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
