package com.antonworks.popularmoviess2;

import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antonworks.popularmoviess2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by sassa_000 on 14.08.2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Movie[] movieSet = null;
    Context mContext;
    public boolean favMode = false;

    interface OnClickItemListener{
        void OnClick(int position,View view);
    }

    private OnClickItemListener itemClickListener;

    public MovieAdapter(OnClickItemListener listener, Context context)
    {
        mContext = context;
        itemClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        itemView.setFocusable(true);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Context context = holder.mMovieThumbImageView.getContext();
        if (favMode)
        {
            Picasso.with(context).load(new File(context.getFilesDir(),movieSet[position].getThumbPath().substring(1))).
                    placeholder(R.drawable.ic_placeholder).into(holder.mMovieThumbImageView);
        }
        else
        {
            String image_path = mContext.getString(R.string.medium_image_url,movieSet[position].getThumbPath());
            Picasso.with(context).load(image_path).placeholder(R.drawable.ic_placeholder).into(holder.mMovieThumbImageView);
        }
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieSet[position].getMovieId())};
        Uri uriMovieId = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieSet[position].getMovieId())).build();

        int count = context.getContentResolver().query(uriMovieId,projection,selection,selectionArgs,null).getCount();
        if(count > 0)
        {
            holder.mFavouriteImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_fav_checked,null));
            //Log.d("LOG_MOVIE","mov exists");
            movieSet[position].setIsFavourite(true);
        }
        else
        {
            holder.mFavouriteImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_fav_unchecked,null));
            //Log.d("LOG_MOVIE","mov not exists");
            movieSet[position].setIsFavourite(false);
        }

    }

    @Override
    public int getItemCount() {
        if (this.movieSet == null)
        {
            return 0;
        }
        return this.movieSet.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMovieThumbImageView;
        private ImageView mFavouriteImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieThumbImageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumb);
            mFavouriteImageView = (ImageView) itemView.findViewById(R.id.iv_favourite);

            itemView.setOnClickListener(this);
            mFavouriteImageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            itemClickListener.OnClick(getAdapterPosition(),v);
        }
    }

    public void setMovieData(Movie[] mSet, boolean favMode)
    {
        this.movieSet = null;
        this.movieSet = mSet;
        this.favMode = favMode;
        notifyDataSetChanged();
    }





}


