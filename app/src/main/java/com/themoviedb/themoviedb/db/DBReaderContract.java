package com.themoviedb.themoviedb.db;

import android.provider.BaseColumns;

public final class DBReaderContract {
    private DBReaderContract(){}

    public static class DBFavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "FAVORITES";
        public static final String COLUMN_NAME_ID = "movie_id";
    }
}
