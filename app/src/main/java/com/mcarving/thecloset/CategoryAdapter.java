package com.mcarving.thecloset;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcarving.thecloset.data.Category;
import com.mcarving.thecloset.data.CategoryTable;
import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * {@Link CategoryAdapter} is an adapter for a list view
 * that uses a {@Link Cursor} of category data as its data source,
 * displaying a category's name and item numbers.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public interface ItemClickListener {
        void onItemClickListener(String name);
    }

    final private ItemClickListener mItemClickListener;

    public CategoryAdapter(Context context, Cursor cursor, ItemClickListener listener) {
        this.mContext = context;
        this.mCursor = cursor;
        mItemClickListener = listener;
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public Category getCategory(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(CategoryTable._ID));
            String name = mCursor.getString(
                    mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME));
            int count = mCursor
                    .getInt(mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_COUNT));
            String imageUrl = mCursor
                    .getString(mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_IMAGE_URL));
            return new Category(id, name, count, imageUrl);
        }

        return null;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        holder.itemId = mCursor.getInt(mCursor.getColumnIndexOrThrow(CategoryTable._ID));
        holder.itemName = mCursor.getString(mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME));
        holder.itemQuantity = mCursor
                .getInt(mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_COUNT));
        holder.itemImageUrl = mCursor
                .getString(mCursor.getColumnIndexOrThrow(CategoryTable.COLUMN_IMAGE_URL));
        holder.categoryName.setText(holder.itemName);

        holder.itemView.setBackgroundColor(Color.WHITE);

        String counts;
        if (holder.itemQuantity >= 2) {
            counts = " items";
        } else {
            counts = " item";
        }
        holder.categoryItemCounts.setText(Integer.toString(holder.itemQuantity) + counts);

        if (holder.itemQuantity == 0) {
            //load the default image
            Picasso.with(mContext)
                    .load(R.drawable.default_image)
                    .into(holder.categoryImage);
        } else {
            File f = new File(holder.itemImageUrl);
            Picasso.with(mContext)
                    .load(f)
                    .into(holder.categoryImage);
        }


    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public int itemId;
        public String itemName;
        public int itemQuantity;
        public String itemImageUrl;

        TextView categoryName;
        TextView categoryItemCounts;
        ImageView categoryImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.tv_category_name);
            categoryItemCounts = itemView.findViewById(R.id.tv_category_counts);
            categoryImage = itemView.findViewById(R.id.iv_category_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClickListener(itemName);
        }
    }
}
