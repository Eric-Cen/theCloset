package com.mcarving.thecloset.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ClothTable implements BaseColumns {

    public static final String PATH_CLOTH = "cloth";

    /** The content URI to access the cloth data in the provider */
    public static final Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_CLOTH)
            .build();

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a list of clothes.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
                    + PATH_CLOTH;

    /**
     * The MIME type of the {@Link #CONTENT_URI} for a single cloth.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                    + DbContract.CONTENT_AUTHORITY + "/"
                    + PATH_CLOTH;

    /**Name of the cloth database table.*/
    public final static String TABLE_NAME = "cloth";

    /**
     * Columns of cloth database table.
     * _id is implied as a subclass of BaseColumns.
     */
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PURCHASE_DATE = "purchase_date";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_CATEGORY = "category";



    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri createClothUriWithId(int itemId){
        return ContentUris.withAppendedId(CONTENT_URI, itemId);
    }

    // table creation SQL statement
    public static final String SQL_CREATE_CLOTH_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_PRICE + " REAL, "
            + COLUMN_PURCHASE_DATE + " TEXT, "
            + COLUMN_BRAND + " TEXT, "
            + COLUMN_SIZE + " TEXT, "
            + COLUMN_STATUS + " TEXT, "
            + COLUMN_IMAGE_URL + " TEXT NOT NULL, "
            + COLUMN_CATEGORY + " TEXT NOT NULL);";
}
