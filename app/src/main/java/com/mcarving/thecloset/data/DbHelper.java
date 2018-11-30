package com.mcarving.thecloset.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mcarving.thecloset.R;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "closet.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CategoryTable.SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(ClothTable.SQL_CREATE_CLOTH_TABLE);
        sqLiteDatabase.execSQL(ToWearItemTable.SQL_CREATE_TOWEAR_TABLE);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Only update the database if it's writable
        if (db.isReadOnly()) {
            return;
        }

        // Only insert initial category data if the category table is empty
        Cursor cursor = db.query(CategoryTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            return;
        }
        cursor.close();

        // Category database is empty. Add some categories.
        String[] names = context.getResources().getStringArray(R.array.category_names);
        for (int i = 0; i < names.length; i++) {
            ContentValues values = new ContentValues();
            values.put(CategoryTable.COLUMN_NAME, names[i]);
            values.put(CategoryTable.COLUMN_COUNT, 0);
            values.put(CategoryTable.COLUMN_IMAGE_URL, "");
            db.insert(CategoryTable.TABLE_NAME, null, values);
        }
        context.getContentResolver().notifyChange(CategoryTable.CONTENT_URI, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          int oldVersion,
                          int newVersion) {
        // no need to implement for first version of database
    }
}
