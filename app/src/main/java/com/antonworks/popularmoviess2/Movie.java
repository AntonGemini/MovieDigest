package com.antonworks.popularmoviess2;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sassa_000 on 16.08.2017.
 */

public class Movie implements Parcelable{
    private int id;
    private String thumbPath;
    private String title;
    private String date;
    private String desc;
    private int length;
    private double rating;
    private int movieId;
    private boolean isFavourite;


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getThumbPath()
    {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath)
    {
        this.thumbPath = thumbPath;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

    public int getLength() { return length; }

    public void setLength(int length) { this.length = length; }

    public double getRating() { return rating; }

    public void setRating(double rating) { this.rating = rating; }

    public int getMovieId() { return movieId; }

    public void setMovieId(int movieId) { this.movieId = movieId; }

    public boolean getIsFavourite() { return isFavourite; }

    public void setIsFavourite(boolean isFavourite) { this.isFavourite = isFavourite; }


    public Movie()
    {

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



    public Movie(Parcel in)
    {
        this.id = in.readInt();
        this.thumbPath = in.readString();
        this.title = in.readString();
        this.date = in.readString();
        this.desc = in.readString();
        this.length = in.readInt();
        this.rating = in.readDouble();
        this.movieId = in.readInt();
        this.isFavourite = in.readByte() != 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.thumbPath);
        dest.writeString(this.title);
        dest.writeString(this.date);
        dest.writeString(this.desc);
        dest.writeInt(this.length);
        dest.writeDouble(this.rating);
        dest.writeInt(this.movieId);
        dest.writeByte((byte)(this.isFavourite ? 1 : 0));
    }
}
