package com.mcarving.thecloset;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mcarving.thecloset.data.ToWearItemTable;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * {@Link ToWearAdapter} is an adapter for a list view
 * that uses a {@Link Cursor} of ToWearItem data as its data source,
 * displaying a category name, cloth name and cloth's image view.
 */
public class ToWearAdapter extends RecyclerView.Adapter<ToWearAdapter.ToWearViewHolder> {

    private static final String TAG = "ToWearAdapter";
    private Context mContext;

    private Cursor mCursor;

    public ToWearAdapter(Context context, Cursor curosr) {
        this.mContext = context;
        this.mCursor = curosr;
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ToWearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_to_wear, parent, false);
        return new ToWearViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToWearViewHolder holder, int position) {
        if (mCursor == null) {
            return;
        }

        mCursor.moveToPosition(position);
        holder.itemId = mCursor.getInt(
                mCursor.getColumnIndexOrThrow(ToWearItemTable._ID));
        holder.itemName = mCursor.getString(
                mCursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CLOTH_NAME));
        holder.itemImageUrl = mCursor.getString(
                mCursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_IMAGE_URL));
        holder.itemClothId = mCursor.getInt(
                mCursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CLOTH_ID));
        holder.itemCategory = mCursor.getString(
                mCursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_CATEGORY_NAME));
        holder.itemDate = mCursor.getString(
                mCursor.getColumnIndexOrThrow(ToWearItemTable.COLUMN_DATE));
        String displayStr = holder.itemCategory + ": " + holder.itemName;

        holder.textViewDescription.setText(displayStr);
        File f = new File(holder.itemImageUrl);
        Picasso.with(mContext)
                .load(f)
                .error(R.drawable.image_not_available)
                .into(holder.imageView);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.itemId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view, int itemId) {
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_to_wear, popup.getMenu());
        popup.setOnMenuItemClickListener(new ToWearMenuItemClickListener(itemId));
        popup.show();
    }

    // Click listener for popup menu items
    class ToWearMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int toWearId;

        public ToWearMenuItemClickListener(int id) {
            toWearId = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_to_wear:
                    String selection = ToWearItemTable._ID + "=?";
                    String[] selectinArgs = {String.valueOf(toWearId)};
                    mContext.getContentResolver().delete(ToWearItemTable.CONTENT_URI,
                            selection,
                            selectinArgs);
                    return true;
                default:
                    return false;

            }

        }
    }

    class ToWearViewHolder extends RecyclerView.ViewHolder {
        public int itemId;
        public String itemName;
        public String itemImageUrl;
        public int itemClothId;
        public String itemCategory;
        public String itemDate;


        ImageView imageView;
        TextView textViewDescription; // categoryName: clothName
        ImageView overflow;

        public ToWearViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_to_wear);
            textViewDescription = itemView.findViewById(R.id.tv_to_wear_description);
            overflow = itemView.findViewById(R.id.iv_overflow_to_wear);
        }

    }
}
