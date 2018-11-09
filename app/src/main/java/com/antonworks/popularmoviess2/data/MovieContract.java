package com.antonworks.popularmoviess2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sassa_000 on 21.09.2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.antonworks.popularmoviess2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES = "movies";



    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }



}
