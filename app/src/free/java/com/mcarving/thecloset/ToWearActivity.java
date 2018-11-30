package com.mcarving.thecloset;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mcarving.thecloset.data.ToWearItemTable;

// activity to show the list view of ToWEarItems
public class ToWearActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ToWearActivity";
    private static final int TOWEAR_LOADER = 1111;

    private ToWearAdapter toWearAdapter;
    private RecyclerView toWearRecyclerView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_wear);
        Toolbar mainToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);

        setTitle("Selected Items for Today:");
        initializeViews();
        toWearRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        toWearAdapter = new ToWearAdapter(this, null);
        toWearRecyclerView.setAdapter(toWearAdapter);

        getSupportLoaderManager().initLoader(TOWEAR_LOADER, null, this);

        //load an ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initializeViews(){
        toWearRecyclerView = (RecyclerView)findViewById(R.id.rv_to_wear);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        return new CursorLoader(this,
                ToWearItemTable.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        toWearAdapter.swapCursor(data);

        if(data!=null){
            Analytics.logEventViewToWearActivity(getApplicationContext(), data.getCount());
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        toWearAdapter.swapCursor(null);

    }
}
