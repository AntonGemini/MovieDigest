package com.antonworks.popularmoviess2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.antonworks.popularmoviess2.data.MovieContract;
import com.antonworks.popularmoviess2.utils.MovieUtils;
import com.antonworks.popularmoviess2.utils.NetworkLoader;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickItemListener, NetworkLoader.OnNetworkLoaderFinished
        {

    private MovieAdapter adapter;
    private Movie[] movieSet;
    RecyclerView mMovieListRecyclerView;

    public static final String MOVIE_URL = "movieUrl";
    private static final String MOVIE_ARRAY = "MOVIE_ARRAY";
    private String sortOrder = NetworkUtils.default_order;

    private final int POPULAR_LOADER_ID = 1;
    private final int RATED_LOADER_ID = 2;
    private final int DB_LOADER_ID = 3;

    private static int mPosition;


    public static final int INDEX_MOVIE_TITLE = 0;
    public static final int INDEX_MOVIE_DATE = 1;
    public static final int INDEX_MOVIE_DESC = 2;
    public static final int INDEX_MOVIE_IMAGE_PATH = 3;
    public static final int INDEX_MOVIE_RATING = 4;
    public static final int INDEX_MOVIE_ID = 5;


    LoaderManager.LoaderCallbacks<Cursor> dbLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = new String[]{
                    MovieContract.MovieEntry.COLUMN_TITLE,
                    MovieContract.MovieEntry.COLUMN_DATE,
                    MovieContract.MovieEntry.COLUMN_DESC,
                    MovieContract.MovieEntry.COLUMN_IMAGE_PATH,
                    MovieContract.MovieEntry.COLUMN_RATING,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID
            };
            return new CursorLoader(getBaseContext(), MovieContract.MovieEntry.CONTENT_URI,projection,null,null, MovieContract.MovieEntry.COLUMN_TITLE);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (sortOrder == getString(R.string.sort_favourite)) {
                movieSet = MovieUtils.ConvertCursorToMovies(data);
                adapter.setMovieData(movieSet, true);
                if (mPosition > 0)
                    mMovieListRecyclerView.smoothScrollToPosition(mPosition);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


            @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);

        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Surface.ROTATION_0 || getResources().getConfiguration().orientation == Surface.ROTATION_180)
        {
            gridLayoutManager = new GridLayoutManager(this,4);
        }
        else{
            gridLayoutManager = new GridLayoutManager(this,2);
        }
        mMovieListRecyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MovieAdapter(this,this);

        mMovieListRecyclerView.setAdapter(adapter);
        mMovieListRecyclerView.setHasFixedSize(true);

        sortOrder = getString(R.string.sort_default);
        if (savedInstanceState != null )
        {
            sortOrder = savedInstanceState.getString(getString(R.string.query_sort));

            Movie[] savedMovies = (Movie[]) savedInstanceState.getParcelableArray(MOVIE_ARRAY);
            if (savedMovies != null && savedMovies.length > 0 && sortOrder != getString(R.string.sort_favourite))
            {

                movieSet = savedMovies;
            }
            else
            {
                getMovieData(sortOrder);
            }

        }
        else
        {
            getMovieData(sortOrder);
        }

    }



    private void getMovieData(String sort){

        if(NetworkUtils.isOnline(this) && sort != getString(R.string.sort_favourite)) {
            URL url = NetworkUtils.buildUrl(sort);
            Bundle bundle = new Bundle();
            bundle.putString(MOVIE_URL,url.toString());

            if (sort == getString(R.string.sort_popular))
                getSupportLoaderManager().restartLoader(POPULAR_LOADER_ID,bundle,new NetworkLoader(this,bundle,this, sort));
            else
                getSupportLoaderManager().restartLoader(RATED_LOADER_ID,bundle,new NetworkLoader(this,bundle,this, sort));
        }
        else if (!NetworkUtils.isOnline(this) && sort != getString(R.string.sort_favourite) ) {
            Toast.makeText(this,R.string.no_internet,Toast.LENGTH_SHORT).show();
        }
        else if (sort.equals(getString(R.string.sort_favourite)) || !NetworkUtils.isOnline(this))  {
            getSupportLoaderManager().restartLoader(DB_LOADER_ID,null,dbLoader);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPosition = -1;
        movieSet = null;
        if (item.getItemId() == R.id.item_popular)
        {
            sortOrder = getResources().getString(R.string.sort_popular);
            getMovieData(sortOrder);
        }
        else if (item.getItemId() == R.id.item_rated) {
            sortOrder = getResources().getString(R.string.sort_rated);
            getMovieData(NetworkUtils.order_top_rated);
        }
        else
        {
            sortOrder = getResources().getString(R.string.sort_favourite);
            getMovieData(sortOrder);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPosition = ((GridLayoutManager)mMovieListRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putString(getString(R.string.query_sort),sortOrder);
        outState.putParcelableArray(MOVIE_ARRAY, movieSet);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieSet != null && sortOrder != getString(R.string.sort_favourite)) {
            adapter.setMovieData(movieSet, false);
        }
    }

            @Override
    public void OnClick(int position, View view) {
        Movie movie = movieSet[position];


        if (view.getId() == R.id.iv_favourite)
        {
            ImageView mFavImageView = (ImageView) view;
            MovieUtils.toggleFavouriteStatus(this,movie, mFavImageView);
        }
        else {
            mPosition = -1;
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.movie_parcel),movie);
            intent.putExtra(getString(R.string.sort_order),sortOrder);
            startActivity(intent);
        }
    }

    @Override
    public void OnLoadFinished(Movie[] data) {
        adapter.setMovieData(data,false);
        if (mPosition < 0) mPosition = 0;
        mMovieListRecyclerView.smoothScrollToPosition(mPosition);
        movieSet = data;
    }

}
