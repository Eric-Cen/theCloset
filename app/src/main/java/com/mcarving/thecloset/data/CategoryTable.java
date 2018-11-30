package com.mcarving.thecloset.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class CategoryTable implements BaseColumns {
    public static final String PATH_CATEGORY = "category";

    /** The content URI to access the category data in the provider */
    public static final Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_CATEGORY)
            .build();

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a list of categories.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
                    + PATH_CATEGORY;

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a single category.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
                    + PATH_CATEGORY;

    /**Name of the cloth database table.*/
    public static final String TABLE_NAME = "categories";

    /**
     * Columns of cloth database table.
     * _id is implied as a subclass of BaseColumns.
     */
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_IMAGE_URL = "image_url";

    public static String getIdFromUri(Uri uri){
        return uri.getLastPathSegment();
    }

    public static Uri createCategoryUriWithId(int itemId){
        return ContentUris.withAppendedId(CONTENT_URI, itemId);
    }

    // table creation SQL statement
    public static final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_COUNT + " INTEGER NOT NULL, "
            + COLUMN_IMAGE_URL + " TEXT );";

}
