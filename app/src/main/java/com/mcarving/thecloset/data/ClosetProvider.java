package com.mcarving.thecloset.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ClosetProvider extends ContentProvider {
    private static final String TAG = "ClosetProvider";

    private DbHelper mDbHelper;

    public static final int MATCH_CODE_CATEGORY = 100;
    public static final int MATCH_CODE_CATEGORY_ID = 101;
    public static final int MATCH_CODE_CLOTH = 102;
    public static final int MATCH_CODE_CLOTH_ID = 103;
    public static final int MATCH_CODE_TO_WEAR = 104;
    public static final int MATCH_CODE_TO_WEAR_ID = 105;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, CategoryTable.PATH_CATEGORY, MATCH_CODE_CATEGORY);
        matcher.addURI(authority, CategoryTable.PATH_CATEGORY + "/#", MATCH_CODE_CATEGORY_ID);

        matcher.addURI(authority, ClothTable.PATH_CLOTH, MATCH_CODE_CLOTH);
        matcher.addURI(authority, ClothTable.PATH_CLOTH + "/#", MATCH_CODE_CLOTH_ID);

        matcher.addURI(authority, ToWearItemTable.PATH_TO_WEAR_ITEM, MATCH_CODE_TO_WEAR);
        matcher.addURI(authority, ToWearItemTable.PATH_TO_WEAR_ITEM + "/#",
                MATCH_CODE_TO_WEAR_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        Cursor cursor;
        final SQLiteDatabase database = mDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MATCH_CODE_CATEGORY:
                cursor = database.query(
                        CategoryTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //groupBy
                        null, // having
                        sortOrder);
                break;
            case MATCH_CODE_CATEGORY_ID:
                String categoryId = CategoryTable.getIdFromUri(uri);
                cursor = database.query(
                        CategoryTable.TABLE_NAME,
                        projection,
                        CategoryTable._ID + " = " + categoryId,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCH_CODE_CLOTH:
                cursor = database.query(
                        ClothTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCH_CODE_CLOTH_ID:
                String clothId = ClothTable.getIdFromUri(uri);
                cursor = database.query(
                        ClothTable.TABLE_NAME,
                        projection,
                        ClothTable._ID + " = " + clothId,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MATCH_CODE_TO_WEAR:
                cursor = database.query(
                        ToWearItemTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MATCH_CODE_TO_WEAR_ID:
                String toWearId = ToWearItemTable.getIdFromUri(uri);
                cursor = database.query(
                        ToWearItemTable.TABLE_NAME,
                        projection,
                        ToWearItemTable._ID + " = " + toWearId,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_CODE_CATEGORY:
                return CategoryTable.CONTENT_LIST_TYPE;
            case MATCH_CODE_CATEGORY_ID:
                return CategoryTable.CONTENT_ITEM_TYPE;
            case MATCH_CODE_CLOTH:
                return ClothTable.CONTENT_LIST_TYPE;
            case MATCH_CODE_CLOTH_ID:
                return ClothTable.CONTENT_ITEM_TYPE;
            case MATCH_CODE_TO_WEAR:
                return ToWearItemTable.CONTENT_LIST_TYPE;
            case MATCH_CODE_TO_WEAR_ID:
                return ToWearItemTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknow URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        long id;
        final String tableName;
        switch (match) {
            case MATCH_CODE_CATEGORY:
                tableName = CategoryTable.TABLE_NAME;
                break;
            case MATCH_CODE_CLOTH:
                tableName = ClothTable.TABLE_NAME;
                break;
            case MATCH_CODE_TO_WEAR:
                tableName = ToWearItemTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }

        id = mDbHelper.getWritableDatabase()
                .insert(tableName, null, contentValues);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final String tableName;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_CODE_CATEGORY:
                tableName = CategoryTable.TABLE_NAME;
                break;
            case MATCH_CODE_CLOTH:
                tableName = ClothTable.TABLE_NAME;
                break;
            case MATCH_CODE_TO_WEAR:
                tableName = ToWearItemTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        rowsDeleted = database.delete(
                tableName,
                selection,
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        final int match = sUriMatcher.match(uri);
        final String tableName;
        switch (match) {
            case MATCH_CODE_CATEGORY:
                tableName = CategoryTable.TABLE_NAME;
                break;
            case MATCH_CODE_CLOTH:
                tableName = ClothTable.TABLE_NAME;
                break;
            case MATCH_CODE_TO_WEAR:
                tableName = ToWearItemTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        rowsUpdated = database.update(
                tableName,
                contentValues,
                selection,
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
