package com.antonworks.popularmoviess2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.antonworks.popularmoviess2.adapters.ReviewAdapter;
import com.antonworks.popularmoviess2.adapters.VideoAdapter;
import com.antonworks.popularmoviess2.databinding.ActivityDetailsBinding;
import com.antonworks.popularmoviess2.utils.MovieUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.VideoClickListener{

    ActivityDetailsBinding mBinding;

    private final String REVIEW_URL = "reviewUrl";
    private final String VIDEO_URL = "videoUrl";
    private final int REVIEWS = 1;
    private final int VIDEOS = 2;
    private static String sortOrder = "";


    LoaderManager.LoaderCallbacks<String> detailsLoader = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<String>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public String loadInBackground() {
                    URL url = null;
                    String reviewsData = null;
                    try {
                        url = id == REVIEWS ? NetworkUtils.buildUrl(args.getString(REVIEW_URL)) : NetworkUtils.buildUrl(args.getString(VIDEO_URL));
                        reviewsData = NetworkUtils.getResponseFromUrl(url);
                    }
                    catch(Exception ex)
                    {
                        Log.e("DETAIL_ERROR",ex.getMessage());
                    }
                    return reviewsData;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {

            if (loader.getId() == REVIEWS)
                bindReviews(data);
            else if (loader.getId() == VIDEOS)
                bindVideos(data);

        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };

    private void bindReviews(String data) {
        String[] reviews = null;
        reviews = MovieUtils.ConvertStringToReviews(data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false);
        mBinding.rvReviews.setAdapter(new ReviewAdapter(reviews,getBaseContext()));
        mBinding.rvReviews.setHasFixedSize(true);
        mBinding.rvReviews.setLayoutManager(linearLayoutManager);
    }

    private void bindVideos(String data) {
        String[] videos = null;
        videos = MovieUtils.ConvertStringToVideos(data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.HORIZONTAL,false);
        mBinding.rvVideos.setAdapter(new VideoAdapter(videos,getBaseContext(),this));
        mBinding.rvVideos.setHasFixedSize(true);
        mBinding.rvReviews.setScrollContainer(true);
        mBinding.rvVideos.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mBinding = DataBindingUtil.setContentView(DetailsActivity.this,R.layout.activity_details);
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.sort_order)))
        {
            sortOrder = intent.getExtras().getString(getString(R.string.sort_order));
        }

        if (intent.hasExtra(getString(R.string.movie_parcel)))
        {
            Bundle data = intent.getExtras();
            Movie movie = data.getParcelable(getString(R.string.movie_parcel));
            bindData(movie);
            if (NetworkUtils.isOnline(getBaseContext()))
            {
                int id = movie.getMovieId();
                getMovieDetails(id);
                Bundle bundle = new Bundle();
                String reviewIdPart = getBaseContext().getString(R.string.review_movie_url,String.valueOf(id));
                String videoIdPart = getBaseContext().getString(R.string.video_movie_url,String.valueOf(id));
                bundle.putString(REVIEW_URL,reviewIdPart);
                bundle.putString(VIDEO_URL,videoIdPart);
                getSupportLoaderManager().restartLoader(REVIEWS,bundle,detailsLoader);
                getSupportLoaderManager().restartLoader(VIDEOS,bundle,detailsLoader);
            }
        }
    }

    private void getMovieDetails(int id)
    {
        if(NetworkUtils.isOnline(getBaseContext()))
        {
            URL url = NetworkUtils.buildUrl(String.valueOf(id));
            new GetDetails().execute(url);
        }
        else {
            Toast.makeText(this,R.string.no_internet,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnClickVideo(String key) throws MalformedURLException {
        String videoUrl = getString(R.string.videoUrl,key);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        if (intent.resolveActivity(getPackageManager())!= null)
        {
            startActivity(intent);
        }
    }


    private class GetDetails extends AsyncTask<URL, Void, String>
    {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String result = "";
            if (url!= null)
            {
                result = NetworkUtils.getResponseFromUrl(url);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject movie = new JSONObject(s);
                int runtime = Integer.parseInt(movie.getString("runtime"),10);
                String runtimeString = DateUtils.formatElapsedTime(runtime * 60);
                mBinding.tvRuntime.setText(runtimeString.substring(0,runtimeString.lastIndexOf(":")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindData(final Movie movie)  {
        String poster_path;
        String[] date;
        Context context = getBaseContext();
        poster_path = movie.getThumbPath();
        date = movie.getDate().split("-");

        if (sortOrder.equals(getString(R.string.sort_favourite)))
        {
            Picasso.with(context).load(new File(context.getFilesDir(),poster_path.substring(1))).
                    placeholder(R.drawable.ic_placeholder).into(mBinding.ivMoviePoster);
        }
        else
        {
            String image_path = getString(R.string.medium_image_url,poster_path);
            Picasso.with(context).load(image_path).placeholder(R.drawable.ic_placeholder).into(mBinding.ivMoviePoster);
        }


        mBinding.tvMovieTitle.setText(movie.getTitle());
        mBinding.tvPlot.setText(movie.getDesc());
        mBinding.tvReleaseDate.setText(date[0]);
        mBinding.tvUserRating.setText(String.valueOf(movie.getRating()));
        mBinding.ivFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView mFavImageView = (ImageView) v;
                MovieUtils.toggleFavouriteStatus(getBaseContext(),movie,mFavImageView);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                sharedPreferences.edit().remove(sortOrder).apply();
                sharedPreferences.edit().putInt(sortOrder,movie.getMovieId()).apply();

            }
        });
        setFavouriteStatus(movie);
    }

    private void setFavouriteStatus(Movie movie) {
        if (movie.getIsFavourite())
        {
            mBinding.ivFavourite.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_fav_checked,null));
        }
        else
        {
            mBinding.ivFavourite.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_fav_unchecked,null));
        }
    }
}
