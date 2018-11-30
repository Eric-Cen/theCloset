package com.mcarving.thecloset;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mcarving.thecloset.data.ClothTable;
import com.mcarving.thecloset.retrofitWeather.WeatherInfoService;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

//activity to show a list of cloth items for a category
public class ClothListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ClothListActivity";

    public static final String CATEGORY_STATE_KEY = "category";
    public static final String CATEGORY_NAME = "category_name";
    private static final int CLOTH_LOADER = 2;
    private static final int GALLERY = 111;
    private static final int CAMERA = 112;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 113;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 114;

    private String categoryName;

    private ClothAdapter clothAdapter;
    private RecyclerView clothRecyclerView;

    private FloatingActionButton fab, fabCamera, fabPhotos;
    private boolean isFABOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //recovering the instance state
        if (savedInstanceState != null) {
            categoryName = savedInstanceState.getString(CATEGORY_STATE_KEY);
        } else {

            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(CATEGORY_NAME)) {
                categoryName = intent.getStringExtra(CATEGORY_NAME);
                //save categoryName to savedInstatanceState
            }
        }


        initializeView();
        clothRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        clothAdapter = new ClothAdapter(this, null);
        clothRecyclerView.setAdapter(clothAdapter);

        if (!categoryName.isEmpty()) {
            // get categoryName from content provider
            // set title
            // initialize contentResorlver
            setTitle(categoryName);

            getSupportLoaderManager().initLoader(CLOTH_LOADER, null, this);
        }

        WeatherInfoService.startWeatherInfoService(getApplicationContext());
    }

    private void initializeView() {
        clothRecyclerView = (RecyclerView) findViewById(R.id.rv_cloth);
        fab = (FloatingActionButton) findViewById(R.id.fab_add_cloth);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabPhotos = (FloatingActionButton) findViewById(R.id.fab_photo_lib);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        //start camera activity to take a photo
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(getApplicationContext(), "take a picture");
                takePhotoFromCamera();
            }
        });

        //open photo app to let use pick a photo from gallery
        fabPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(getApplicationContext(), "pick from photo");
                choosePhotoFromGallary();
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabPhotos.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fabCamera.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabPhotos.animate().translationY(0);
        fabCamera.animate().translationY(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cloth_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selected_items:
                Intent selectedIntent = new Intent(this, ToWearActivity.class);
                startActivity(selectedIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putString(CATEGORY_STATE_KEY, categoryName);
        //call superclass to save any view hierachy
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ClothTable._ID,
                ClothTable.COLUMN_IMAGE_URL,
                ClothTable.COLUMN_NAME,
                ClothTable.COLUMN_CATEGORY
        };

        String selection = ClothTable.COLUMN_CATEGORY + "=?";
        String[] selectionArgs = {categoryName};

        return new CursorLoader(this,
                ClothTable.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: cursor.getCount() = " + data.getCount());
        clothAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        clothAdapter.swapCursor(null);
    }

    // REQUEST PERMISSION TO EXTERNAL STORAGE
    private void choosePhotoFromGallary() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Permissionis not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(ClothListActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ClothListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            loadFromGallery();
        }
    }

    // allows user to import cloth image from Photo galleries
    private void loadFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    // checks camera permission and launch launchCamera() method
    private void takePhotoFromCamera() {
        //check Camera permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(ClothListActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to user *asynchronously* -- don't block
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ClothListActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

        } else {
            launchCamera();
        }
    }

    // allows user to take a picture from camera
    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    // permission denied,
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFromGallery();
                } else {
                    // permission denied.
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            // do nothing, stay on ClothListActivity
            return;
        }

        Bitmap bitmap = null;

        if (requestCode == GALLERY) {
            // how to handle image
            if (data != null) {
                Uri contentUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(this.getContentResolver(), contentUri);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

        if (requestCode == CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (bitmap != null) {
            String imagePath = "";

            imagePath = saveToInternalStorage(bitmap);


            if (imagePath.isEmpty()) {
                Utils.showToast(getApplicationContext(), "failed to save image");
                return;
            } else {
                // start AddClothActivity with the bitmap uri as the extra?
                // as extra to new activity

                Intent intent = new Intent(getApplicationContext(),
                        AddClothActivity.class);

                intent.putExtra(AddClothActivity.PATH, imagePath);
                intent.putExtra(CATEGORY_NAME, categoryName);
                startActivity(intent);
            }
        }

    }

    //save imported image into internal storage, and returns the path url as string
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/theCloset/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String fileName = Calendar.getInstance().getTimeInMillis() + ".jpg";
        // Create ImageDir
        File myPath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);

            // use the compress method on the Bitmap object to write image
            // to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myPath.getAbsolutePath();
    }
}