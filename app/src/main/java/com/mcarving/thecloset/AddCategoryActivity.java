package com.mcarving.thecloset;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mcarving.thecloset.data.CategoryTable;

// this activity allows user to create a new category in category list
public class AddCategoryActivity extends AppCompatActivity {
    private static final String TAG = "AddCategoryActivity";

    private EditText editTextAddCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
    }

    private void initializeViews() {
        editTextAddCategory = (EditText) findViewById(R.id.et_add_category);
        Button addButton = (Button) findViewById(R.id.btn_add_category);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = getCategoryName();

                if (name != null) {
                    // save new category name to content provider/database
                    saveCategoryName(name);
                    finish();
                }
            }
        });
    }

    // get user inpput and check if it meets the requirement
    private String getCategoryName() {
        String userInput = editTextAddCategory.getText().toString();

        //if the user didn't provide a name
        // or the name is too long, >50 charactors
        if ("".equals(userInput)) {
            Utils.showToast(this, "There is nothing to save, please enter a name");
        } else if (userInput.length() > 50) {
            Utils.showToast(this, "The entered category name is too long!!");
        } else {
            // returns the userinput that meets the requirements
            return userInput;
        }
        return null;
    }

    // save category name into database
    private void saveCategoryName(String categoryName) {
        ContentValues values = new ContentValues();

        values.put(CategoryTable.COLUMN_NAME, categoryName);
        values.put(CategoryTable.COLUMN_COUNT, 0);
        values.put(CategoryTable.COLUMN_IMAGE_URL, "");

        getContentResolver().insert(CategoryTable.CONTENT_URI, values);
    }
}
