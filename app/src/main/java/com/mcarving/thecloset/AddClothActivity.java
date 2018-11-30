package com.mcarving.thecloset;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.mcarving.thecloset.data.CategoryTable;
import com.mcarving.thecloset.data.ClothTable;

// activity to let user add detail cloth information for the new cloth item
public class AddClothActivity extends AppCompatActivity {
    private static final String TAG = "AddClothActivity";

    public static final String PATH = "path";
    private static final String CATEGORY_STATE_KEY = "category";
    private static final String IMAGE_PATH_STATE_KEY = "image_url";

    private String imagePath;
    private String categoryName;

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
        setContentView(R.layout.activity_add_cloth);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            categoryName = savedInstanceState.getString(CATEGORY_STATE_KEY);
            imagePath = savedInstanceState.getString(IMAGE_PATH_STATE_KEY);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PATH)
                && intent.hasExtra(ClothListActivity.CATEGORY_NAME)) {
            imagePath = intent.getStringExtra(PATH);
            categoryName = intent.getStringExtra(ClothListActivity.CATEGORY_NAME);

            setTitle("Add item to " + categoryName);
        }

        initializeViews();
    }

    private void initializeViews() {
        imageView = (ImageView) findViewById(R.id.iv_add_cloth_image);
        imageView.setImageURI(Uri.parse(imagePath));

        editTextName = (EditText) findViewById(R.id.et_add_cloth_name);
        editTextPrice = (EditText) findViewById(R.id.et_add_cloth_price);
        editTextPurchaseDate = (EditText) findViewById(R.id.et_add_cloth_purchase_date);
        editTextBrand = (EditText) findViewById(R.id.et_add_cloth_brand);
        editTextSize = (EditText) findViewById(R.id.et_add_cloth_size);
        editTextDescription = (EditText) findViewById(R.id.et_add_cloth_description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_cloth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_cloth_item:
                // save the data to database via content provider
                if (saveToDatabase()) {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // get user inputs, check if the inputs meet requirement
    // if yes, save to database
    public boolean saveToDatabase() {
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

        if (!validateSave || categoryName.isEmpty()) {
            return false;
        } else {
            String[] projection = {
                    CategoryTable._ID,
                    CategoryTable.COLUMN_NAME,
                    CategoryTable.COLUMN_COUNT,
                    CategoryTable.COLUMN_IMAGE_URL};
            String selection = CategoryTable.COLUMN_NAME + "=?";
            String[] selectionArgs = {categoryName};

            Cursor category = getContentResolver().query(CategoryTable.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            if (category != null) {
                //save new cloth item to database via content provider
                ContentValues values = new ContentValues();
                values.put(ClothTable.COLUMN_NAME, name);
                values.put(ClothTable.COLUMN_DESCRIPTION, description);
                values.put(ClothTable.COLUMN_PRICE, price);
                values.put(ClothTable.COLUMN_PURCHASE_DATE, purchaseDate);
                values.put(ClothTable.COLUMN_BRAND, brand);
                values.put(ClothTable.COLUMN_SIZE, size);
                values.put(ClothTable.COLUMN_STATUS, "");
                values.put(ClothTable.COLUMN_IMAGE_URL, imagePath);
                values.put(ClothTable.COLUMN_CATEGORY, categoryName);
                getContentResolver().insert(ClothTable.CONTENT_URI, values);

                // update category database
                // get the counts, add +1, update counts
                category.moveToFirst();
                int numCount = category.getInt(
                        category.getColumnIndexOrThrow(CategoryTable.COLUMN_COUNT));
                category.close();

                ContentValues contentValues = new ContentValues();
                contentValues.put(CategoryTable.COLUMN_NAME, categoryName);

                if (numCount == 0) {  // count = 0, set imagePath to the category URL
                    contentValues.put(CategoryTable.COLUMN_IMAGE_URL, imagePath);
                }
                ++numCount;
                contentValues.put(CategoryTable.COLUMN_COUNT, numCount);

                getContentResolver().update(CategoryTable.CONTENT_URI,
                        contentValues,
                        selection,
                        selectionArgs);
            }
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(CATEGORY_STATE_KEY, categoryName);
        outState.putString(IMAGE_PATH_STATE_KEY, imagePath);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
