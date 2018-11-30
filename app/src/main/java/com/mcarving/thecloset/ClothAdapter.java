package com.mcarving.thecloset;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcarving.thecloset.data.CategoryTable;
import com.mcarving.thecloset.data.ClothTable;
import com.mcarving.thecloset.data.MyPreferences;
import com.mcarving.thecloset.data.ToWearItem;
import com.mcarving.thecloset.data.ToWearItemTable;
import com.mcarving.thecloset.widget.WidgetUpdateService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * {@Link ClothAdapter} is an adapter for a list view
 * that uses a {@Link Cursor} of cloth data as its data source,
 * displaying a cloth name and image view.
 */

public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ClothViewHolder> {
    private static final String TAG = "ClothAdapter";

    private Context mContext;
    private Cursor mCursor;

    public ClothAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cloth, parent, false);
        return new ClothViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClothViewHolder holder, final int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        holder.itemId = mCursor.getInt(mCursor.getColumnIndexOrThrow(ClothTable._ID));
        holder.itemName = mCursor.getString(mCursor.getColumnIndexOrThrow(ClothTable.COLUMN_NAME));
        holder.itemImageUrl = mCursor
                .getString(mCursor.getColumnIndexOrThrow(ClothTable.COLUMN_IMAGE_URL));
        holder.itemCategory = mCursor
                .getString(mCursor.getColumnIndexOrThrow(ClothTable.COLUMN_CATEGORY));

        holder.textViewName.setText(holder.itemName);
        //Log.d(TAG, "onBindViewHolder: itemName = " + holder.itemName);
        //Log.d(TAG, "onBindViewHolder: itemImageUrl = " + holder.itemImageUrl);

        //holder.imageView.setImageURI(Uri.parse(holder.itemImageUrl));
        File f = new File(holder.itemImageUrl);
        Picasso.with(mContext)
                .load(f)
                .into(holder.imageView);
//it doesn't work with Uri loading ??!
//        Picasso.with(mContext)
//                .load(Uri.parse(holder.itemImageUrl))
//                .into(holder.imageView);

//        Picasso.with(mContext)
//                .load(holder.itemImageUrl)
//                .into(holder.imageView);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,
                        holder.itemId,
                        holder.itemName,
                        holder.itemImageUrl,
                        holder.itemCategory);
            }
        });

    }

    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view, int clothId, String name,
                               String imageUrl, String category) {
        // inflate menu

        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cloth, popup.getMenu());
        popup.setOnMenuItemClickListener(
                new MyMenuItemClickListener(clothId, name, imageUrl, category));
        popup.show();

    }

    // Click listener for popup menu items
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int clothId;
        private String clothName;
        private String imageUrl;
        private String categoryName;

        public MyMenuItemClickListener(int id, String name, String imagePath, String category) {
            clothId = id;
            clothName = name;
            imageUrl = imagePath;
            categoryName = category;
        }

        public ArrayList<ToWearItem> generateToWearItems() {
            ArrayList<ToWearItem> newList = new ArrayList<>();

            String timeStamp = new SimpleDateFormat(MyPreferences.DISPLAY_TIME_PATTERN)
                    .format(Calendar.getInstance().getTime());
            String toWearSelection = ToWearItemTable.COLUMN_DATE + "=?";
            String[] toWearSelectionArgs = {timeStamp};
            Cursor cursor = mContext.getContentResolver().query(ToWearItemTable.CONTENT_URI,
                    null,
                    toWearSelection,
                    toWearSelectionArgs,
                    null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CLOTH_NAME));
                    String imageUrl = cursor.getString(
                            cursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_IMAGE_URL));
                    int clothId = cursor.getInt(
                            cursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CLOTH_ID));
                    String category = cursor.getString(
                            cursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CATEGORY_NAME));
                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_DATE));

                    ToWearItem newItem = new ToWearItem(date, category, name, clothId, imageUrl);

                    newList.add(newItem);
                } while (cursor.moveToNext());

                cursor.close();
            }

            return newList;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_add_to_wear:

                    // update widget UI after user pick a cloth item
                    // check if item is already in the toWearlist on the current day
                    // by ClothId

                    String timeStamp = new SimpleDateFormat(MyPreferences.DISPLAY_TIME_PATTERN)
                            .format(Calendar.getInstance().getTime());
                    String toWearSelection = ToWearItemTable.COLUMN_CLOTH_ID
                            + "=? and "
                            + ToWearItemTable.COLUMN_DATE + "=?";
                    String[] toWearSelectionArgs = {String.valueOf(clothId), timeStamp};
                    Cursor toWearCursor = mContext.getContentResolver()
                            .query(ToWearItemTable.CONTENT_URI,
                                    null,
                                    toWearSelection,
                                    toWearSelectionArgs,
                                    null
                            );
                    int duplicate = -1;
                    if (toWearCursor != null) {
                        duplicate = toWearCursor.getCount();
                        //Log.d(TAG, "onMenuItemClick: toWearCursor.getCount() = " + duplicate);
                        toWearCursor.close();
                    } else {
                        Log.d(TAG, "onMenuItemClick: toWearCursor is empty");
                    }

                    if (duplicate <= 0) {

                        Analytics.logEventSelectContentClothListActivity(mContext,
                                categoryName, clothName);

                        // no duplicate, add to database
                        ContentValues values = new ContentValues();
                        values.put(ToWearItemTable.COLUMN_CLOTH_NAME, clothName);
                        values.put(ToWearItemTable.COLUMN_IMAGE_URL, imageUrl);
                        values.put(ToWearItemTable.COLUMN_CLOTH_ID, clothId);
                        values.put(ToWearItemTable.COLUMN_CATEGORY_NAME, categoryName);
                        values.put(ToWearItemTable.COLUMN_DATE, timeStamp);

                        mContext.getContentResolver().insert(ToWearItemTable.CONTENT_URI, values);
                        Utils.showToast(mContext, "added to wear today");
                    } else {
                        Utils.showToast(mContext, "This item is already on the list.");
                    }

                    // update weather information
                    WidgetUpdateService.startActionUpdateWidget(mContext,
                            MyPreferences.getWeatherInfo(mContext),
                            generateToWearItems()
                    );
                    return true;
                case R.id.action_view_detail:
                    // start ClothDetailActivit, pass cloth Id as extra
                    Intent intent = new Intent(mContext,
                            ClothDetailActivity.class);
                    intent.putExtra(ClothDetailActivity.CLOTH_ID_EXTRA, clothId);
                    mContext.startActivity(intent);

                    return true;
                case R.id.action_delete:
                    Utils.showToast(mContext, "to delete");
                    // add for confirmation
                    // delete from cloth database
                    String selection = ClothTable.COLUMN_IMAGE_URL + "=?";
                    String[] selectionArgs = {imageUrl};
                    mContext.getContentResolver().delete(ClothTable.CONTENT_URI,
                            selection,
                            selectionArgs);

                    // delelte image file
                    File fdelete = new File(imageUrl);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d(TAG, "onMenuItemClick: file deleted: " + imageUrl);
                        } else {
                            Log.d(TAG, "onMenuItemClick: file not deleted" + imageUrl);
                        }
                    }
                    // update count from category
                    String[] projection = {
                            CategoryTable._ID,
                            CategoryTable.COLUMN_NAME,
                            CategoryTable.COLUMN_COUNT,
                            CategoryTable.COLUMN_IMAGE_URL};
                    String selection2 = CategoryTable.COLUMN_NAME + "=?";
                    String[] selectionArgs2 = {categoryName};

                    Cursor category = mContext.getContentResolver()
                            .query(CategoryTable.CONTENT_URI,
                                    projection,
                                    selection2,
                                    selectionArgs2,
                                    null);

                    if (category != null) {
                        category.moveToFirst();
                        int numCount = category.getInt(
                                category.getColumnIndexOrThrow(CategoryTable.COLUMN_COUNT));
                        category.close();

                        --numCount;
                        ContentValues values = new ContentValues();
                        values.put(CategoryTable.COLUMN_COUNT, numCount);

                        // update the image link in category if the first item
                        // of cloth list was deleted
                        String selection3 = ClothTable.COLUMN_CATEGORY + "=?";
                        String[] selectionArgs3 = {categoryName};
                        Cursor clothCursor = mContext.getContentResolver()
                                .query(ClothTable.CONTENT_URI,
                                        null,
                                        selection3,
                                        selectionArgs3,
                                        null);
                        // set image link to "" if there isn't any cloth item
                        String categoryImageUrl = "";
                        if (clothCursor != null && clothCursor.getCount() != 0) {
                            clothCursor.moveToFirst();
                            categoryImageUrl = clothCursor.getString(
                                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_IMAGE_URL));
                            clothCursor.close();
                        }

                        values.put(CategoryTable.COLUMN_IMAGE_URL, categoryImageUrl);

                        mContext.getContentResolver().update(CategoryTable.CONTENT_URI,
                                values,
                                selection2,
                                selectionArgs2);
                    }
                    return true;
                default:
                    return false;
            }

        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    static class ClothViewHolder extends RecyclerView.ViewHolder {

        public int itemId;
        public String itemName;
        public String itemImageUrl;
        public String itemCategory;

        ImageView imageView;
        TextView textViewName;
        ImageView overflow;

        public ClothViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_cloth);
            textViewName = itemView.findViewById(R.id.tv_cloth_name);
            overflow = itemView.findViewById(R.id.iv_overflow);
        }
    }
}
