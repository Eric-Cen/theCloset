package com.mcarving.thecloset;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.mcarving.thecloset.data.Cloth;
import com.mcarving.thecloset.data.ClothTable;

// activity to show detail cloth informtion about a cloth item
// user can modify and save the cloth information
public class ClothDetailActivity extends AppCompatActivity {

    public static final String CLOTH_ID_EXTRA = "cloth_id";

    private static final String TAG = "ClothDetailActivity";
    private static final String CLOTH_ID_STATE_KEY = "cloth_id";
    private static final String CLOTH_ITEM_STATE_KEY = "cloth_item";

    private int clothId;
    private Cloth clothItem;

    ImageView imageView;
    EditText editTextName;
    EditText editTextPrice;
    EditText editTextPurchaseDate;
    EditText editTextBrand;
    EditText editTextSize;
    EditText editTextDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloth_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            clothId = savedInstanceState.getInt(CLOTH_ID_STATE_KEY);
            clothItem = savedInstanceState.getParcelable(CLOTH_ITEM_STATE_KEY);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CLOTH_ID_EXTRA)) {
            clothId = intent.getIntExtra(CLOTH_ID_EXTRA, -1);

            //setTitle("Add item to " + categoryName);
        }

        if (clothId >= 0 && clothItem == null) {

            String selection = ClothTable._ID + "=" + clothId;

            Cursor clothCursor = getContentResolver().query(ClothTable.CONTENT_URI,
                    null,
                    selection,
                    null,
                    null);
            clothCursor.moveToFirst();
            int clothId = clothCursor.getInt(
                    clothCursor.getColumnIndexOrThrow(ClothTable._ID));
            String clothName = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_NAME));
            String description = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_DESCRIPTION));
            double price = clothCursor.getDouble(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_PRICE));
            String purchaseDate = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_PURCHASE_DATE));
            String brand = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_BRAND));
            String size = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_SIZE));
            String categoryName = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_CATEGORY));
            String imageUrl = clothCursor.getString(
                    clothCursor.getColumnIndexOrThrow(ClothTable.COLUMN_IMAGE_URL));

            clothCursor.close();

            clothItem = new Cloth(clothId,
                    clothName,
                    description,
                    price,
                    purchaseDate,
                    brand,
                    size,
                    "",
                    imageUrl,
                    categoryName);

            initializeViews();

        } else {
            Utils.showToast(this, "invalid cloth id..");
            finish();
        }


    }

    private void initializeViews() {
        imageView = (ImageView) findViewById(R.id.iv_cloth_detail_image);
        imageView.setImageURI(Uri.parse(clothItem.getImageUrl()));

        editTextName = (EditText) findViewById(R.id.et_cloth_detail_name);
        editTextName.setText(clothItem.getName());

        editTextPrice = (EditText) findViewById(R.id.et_cloth_detail_price);
        editTextPrice.setText(String.valueOf(clothItem.getPrice()));

        editTextPurchaseDate = (EditText) findViewById(R.id.et_cloth_detail_purchase_date);
        editTextPurchaseDate.setText(clothItem.getPurchaseDate());

        editTextBrand = (EditText) findViewById(R.id.et_cloth_detail_brand);
        editTextBrand.setText(clothItem.getBrand());

        editTextSize = (EditText) findViewById(R.id.et_cloth_detail_size);
        editTextSize.setText(clothItem.getSize());

        editTextDescription = (EditText) findViewById(R.id.et_cloth_detail_description);
        editTextDescription.setText(clothItem.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cloth_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_cloth_detail:
                // save the cloth data to database
                if (saveToDatabase()) {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    // save cloth information to database
    private boolean saveToDatabase() {

        // check inputs and save
        String name; // check if it is not empty
        Double price = 0.0; // check if it contains number only
        String purchaseDate;
        String brand;
        String size;
        String description;
        boolean validateSave = true;

        name = editTextName.getText().toString();
        if (name == null || name.isEmpty()) {
            Utils.showToast(this, "Please enter a name");
            validateSave = false;
        }
        try {
            String priceString = editTextPrice.getText().toString();
            price = Double.parseDouble(priceString);

        } catch (RuntimeException e) {
            e.printStackTrace();
            validateSave = false;
            Utils.showToast(this, "Please enter a value price number");
        }

        purchaseDate = editTextPurchaseDate.getText().toString();
        brand = editTextBrand.getText().toString();
        size = editTextSize.getText().toString();
        description = editTextDescription.getText().toString();

        if (!validateSave) {
            return false;
        } else {

            String selection = ClothTable._ID + "=" + clothId;

            //save new cloth item to database via content provider
            ContentValues values = new ContentValues();
            values.put(ClothTable.COLUMN_NAME, name);
            values.put(ClothTable.COLUMN_DESCRIPTION, description);
            values.put(ClothTable.COLUMN_PRICE, price);
            values.put(ClothTable.COLUMN_PURCHASE_DATE, purchaseDate);
            values.put(ClothTable.COLUMN_BRAND, brand);
            values.put(ClothTable.COLUMN_SIZE, size);
            values.put(ClothTable.COLUMN_STATUS, "");
            values.put(ClothTable.COLUMN_IMAGE_URL, clothItem.getImageUrl());
            values.put(ClothTable.COLUMN_CATEGORY, clothItem.getCategoryName());
            getContentResolver().update(ClothTable.CONTENT_URI,
                    values,
                    selection,
                    null);

            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CLOTH_ID_STATE_KEY, clothId);
        outState.putParcelable(CLOTH_ITEM_STATE_KEY, clothItem);
        super.onSaveInstanceState(outState);
    }
}
