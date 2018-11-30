package com.mcarving.thecloset;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mcarving.thecloset.data.Category;
import com.mcarving.thecloset.data.MyPreferences;
import com.mcarving.thecloset.data.CategoryTable;

import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

//
// activity to let user sign in with email address or google account
// to show list of category names
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CategoryAdapter.ItemClickListener {
    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 1;
    private static final int MY_PERMISSION_REQUEST_INTERNET = 123;
    private static final int CATEGORY_LOADER = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CategoryAdapter categoryAdapter;
    private RecyclerView categoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.INTERNET)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! after the user
                // sees the explanation, try again to request the permission.
            } else {
                // no explanation neeeded; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSION_REQUEST_INTERNET);
            }
        } else {
            // Permission has already been granted
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                String username = "";
                if (user != null) {

                    username = user.getDisplayName();
                    onSignedInInitialize(username);
                } else {
                    // user is signed out
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN
                    );
                    onSignedOutCleanUp(username);

                }
            }
        };

        initializeView();
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        categoryAdapter = new CategoryAdapter(this, null, MainActivity.this);
        categoryRecyclerView.setAdapter(categoryAdapter);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();

        // Kick off the loader
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

    }

    private void initializeView() {
        categoryRecyclerView = (RecyclerView) findViewById(R.id.rv_category);
    }

    // user just signed in, load the data or initialize specific views
    private void onSignedInInitialize(String username) {
        Analytics.logEventLoginMainActivity(this, username);

        //Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();
    }

    // user just signed out, clear the data if necessary
    private void onSignedOutCleanUp(String username) {
        //Toast.makeText(this, "Bye " + username, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "signed out", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.add_category:
                // start a new activity to add category
                Intent intent = new Intent(getApplicationContext(),
                        AddCategoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_selected_items:
                Intent selectedIntent = new Intent(this, ToWearActivity.class);
                startActivity(selectedIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_INTERNET: {
                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onItemClickListener(String name) {
        Intent intent = new Intent(getApplicationContext(),
                ClothListActivity.class);
        intent.putExtra(ClothListActivity.CATEGORY_NAME, name);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                CategoryTable._ID,
                CategoryTable.COLUMN_NAME,
                CategoryTable.COLUMN_COUNT,
                CategoryTable.COLUMN_IMAGE_URL
        };

        return new CursorLoader(this,
                CategoryTable.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        categoryAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        categoryAdapter.swapCursor(null);
    }


    //using nemanja-kovacevic's example code from github, recycler-view-swipe-to-delete
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    //to cache these and not allocate anything repeatedly in the onChildDraw method
                    Drawable background;
                    Drawable xMark;
                    int xMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.RED);
                        xMark = ContextCompat.getDrawable(MainActivity.this,
                                R.drawable.ic_clear__24dp);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        xMarkMargin = (int) MainActivity.this.getResources().getDimension(
                                R.dimen.ic_clear_margin);
                        initiated = true;
                    }

                    // no need to drag & drop
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }


                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int swipedPosition = viewHolder.getAdapterPosition();

                        Category category = categoryAdapter.getCategory(swipedPosition);
                        if (category.getCategoryItemCount() >= 1) {
                            categoryAdapter.notifyItemChanged(swipedPosition);

                            Utils.showToast(getApplicationContext(),
                                    category.getCategoryName() + " category is not empty.  " +
                                            "Remove all clothing item and then try deleting again");
                        } else {
                            // remove the category from database
                            Utils.showToast(getApplicationContext(),
                                    "to delete the category");

                            String selection = CategoryTable.COLUMN_NAME + "=?";
                            String[] selectionArgs = {category.getCategoryName()};
                            getContentResolver().delete(CategoryTable.CONTENT_URI,
                                    selection,
                                    selectionArgs);
                        }
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        // draw red background
                        background.setBounds(itemView.getRight() + (int) dX,
                                itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        // draw x mark
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = xMark.getIntrinsicWidth();
                        int intrinsicHeight = xMark.getIntrinsicHeight();

                        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                        int xMarkRight = itemView.getRight() - xMarkMargin;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                        xMark.draw(c);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                                actionState, isCurrentlyActive);
                    }
                };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(categoryRecyclerView);
    }

    //draw red background in the empty space while the items are animating to thier
    // after an item is removed.
    private void setUpAnimationDecoratorHelper() {
        categoryRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!initiated) {
                    init();
                }

                // only if animationis in progress
                if (parent.getItemAnimator().isRunning()) {
                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;


                    //this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            //view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom()
                                + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop()
                                + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom()
                                + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop()
                                + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);
                }
                super.onDraw(c, parent, state);
            }
        });
    }
}
