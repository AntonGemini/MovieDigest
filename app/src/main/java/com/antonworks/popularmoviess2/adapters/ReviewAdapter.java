package com.antonworks.popularmoviess2.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.antonworks.popularmoviess2.R;

/**
 * Created by sassa_000 on 08.10.2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    String[] reviews = null;
    Context mContext;

    public ReviewAdapter(String[] reviews, Context context)
    {
        this.reviews = reviews;
        mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.mReviewTextView.setText(reviews[position].trim());
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mReviewTextView;
        public Button mShowMoreButton;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewTextView = (TextView) itemView.findViewById(R.id.tv_review);
            mShowMoreButton = (Button) itemView.findViewById(R.id.bt_show_more);
            mShowMoreButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mReviewTextView.getMaxLines() == Integer.MAX_VALUE)
                mReviewTextView.setMaxLines(mContext.getResources().getInteger(R.integer.review_max_lines));
            else
                mReviewTextView.setMaxLines(Integer.MAX_VALUE);
        }
    }

}
