package com.antonworks.popularmoviess2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antonworks.popularmoviess2.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

/**
 * Created by sassa_000 on 10.10.2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    String[] videos = null;
    Context mContext = null;
    public interface VideoClickListener{
        void OnClickVideo(String url) throws MalformedURLException;
    }

    private VideoClickListener videoClickListener;

    public VideoAdapter(String[] videos, Context context, VideoClickListener listener)
    {
        this.videos = videos;
        mContext = context;
        videoClickListener = listener;
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item,parent,false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        String path = mContext.getString(R.string.video_thumbnail,videos[position]);
        Picasso.with(mContext).load(path).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;

        public VideoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv = (ImageView) itemView.findViewById(R.id.iv_video);
        }

        @Override
        public void onClick(View v) {
            try {
                videoClickListener.OnClickVideo(videos[getAdapterPosition()]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
