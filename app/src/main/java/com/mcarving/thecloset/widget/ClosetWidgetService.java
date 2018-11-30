package com.mcarving.thecloset.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mcarving.thecloset.data.ToWearItem;

import java.util.ArrayList;

public class ClosetWidgetService extends RemoteViewsService {

    private static final String TAG = "ClosetWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ClosetRemoteViewsFactory(this, intent);
    }
}

class ClosetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "ClosetRemoteViewsFactor";
    public static final String DATA_EXTRA = "data";
    public static final String TO_WEAR_ITEMS_EXTRA = "to wear list";

    Context mContext;
    ArrayList<ToWearItem> toWearItems;

    public ClosetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;

        Bundle extras = intent.getBundleExtra(DATA_EXTRA);
        if (extras == null) {
            Log.d(TAG, "ClosetRemoteViewsFactory: extras is null");
        } else {
            toWearItems = extras.getParcelableArrayList(TO_WEAR_ITEMS_EXTRA);
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        // how to initialize toWearItem list
        toWearItems = ClosetWidgetProvider.toWearList;
    }

    @Override
    public void onDestroy() {
    }


    @Override
    public int getCount() {
        return toWearItems == null ? 0 : toWearItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (toWearItems == null || toWearItems.isEmpty()) {
            Log.d(TAG, "getViewAt: toWearItems = null");
            return null;
        } else {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    android.R.layout.simple_list_item_1);
            ToWearItem toWearItem = toWearItems.get(position);
            remoteViews.setTextViewText(android.R.id.text1,
                    toWearItem.getCategoryName()
                            + ": "
                            + toWearItem.getClothName()
            );
            return remoteViews;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;  // Treat all items in the ListView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
