package com.antonworks.popularmoviess2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.antonworks.popularmoviess2.MainActivity;
import com.antonworks.popularmoviess2.Movie;
import com.antonworks.popularmoviess2.NetworkUtils;
import com.antonworks.popularmoviess2.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ASassa on 17.10.2017.
 */

public class NetworkLoader implements LoaderManager.LoaderCallbacks<Movie[]> {

    Context mContext;
    SharedPreferences.OnSharedPreferenceChangeListener listener = null;
    private String mSort = "";

    public interface OnNetworkLoaderFinished{
        void OnLoadFinished(Movie[] movieSet);
    }

    private OnNetworkLoaderFinished mListener;

    public NetworkLoader(Context context, Bundle bundle, OnNetworkLoaderFinished listener, String sort)
    {
        mContext = context;
        mListener = listener;
        mSort = sort;
    }

    @Override
    public Loader<Movie[]> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Movie[]>(mContext) {
            Movie[] mMoviesList;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            @Override
            protected void onStartLoading() {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
                super.onStartLoading();
                if (listener == null) {
                    listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                            if (key.equals(mSort)) {
                                onContentChanged();
                            }
                        }
                    };

                }
                sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

                if (mMoviesList == null) {
                    forceLoad();
                } else {
                    deliverResult(mMoviesList);
                }
            }

            @Override
            public void deliverResult(Movie[] data) {
                if (isReset()) {
                    return;
                }


                if (takeContentChanged())
                    onLoadFinished(this, data);
                else
                    super.deliverResult(data);
                mMoviesList = data;
            }

            @Override
            protected void onReset() {
                onStopLoading();
                if (mMoviesList != null) {
                    mMoviesList = null;
                }

            }


            @Override
            public Movie[] loadInBackground() {

                URL url = null;
                try {
                    url = new URL(args.getString(MainActivity.MOVIE_URL));
                } catch (MalformedURLException ex) {
                    Log.e(mContext.getString(R.string.log_tag), ex.getMessage());
                }
                String moviesString = NetworkUtils.getResponseFromUrl(url);
                return MovieUtils.ConvertStringToMovies(moviesString);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mListener.OnLoadFinished(data);

    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}
