package com.mcarving.thecloset.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ToWearItemTable implements BaseColumns {
    public static final String PATH_TO_WEAR_ITEM = "toWear";

    /**
     * The content URI to access the category data in the provider
     */
    public static final Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_TO_WEAR_ITEM)
            .build();

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a list of categories.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
                    + PATH_TO_WEAR_ITEM;

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a single category.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
            + PATH_TO_WEAR_ITEM;

    /**Name of the ToWearItem Database table.*/
    public static final String TABLE_NAME = "toWear";

    /**
     * Columns of cloth database table.
     * _id is implied as a subclass of BaseColumns.
     */
    public static final String COLUMN_CLOTH_NAME = "cloth_name";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_CLOTH_ID = "cloth_id";
    public static final String COLUMN_CATEGORY_NAME = "catergory_name";
    public static final String COLUMN_DATE = "date";

    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri createToWearItemUriWithId(int itemId){
        return ContentUris.withAppendedId(CONTENT_URI, itemId);
    }


    // table creation SQL statement
    public static final String SQL_CREATE_TOWEAR_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CLOTH_NAME + " TEXT NOT NULL, "
            + COLUMN_IMAGE_URL + " TEXT NOT NULL, "
            + COLUMN_CLOTH_ID + " TEXT NOT NULL, "
            + COLUMN_CATEGORY_NAME + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL);";
}
