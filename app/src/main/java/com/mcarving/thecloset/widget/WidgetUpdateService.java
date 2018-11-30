package com.mcarving.thecloset.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.mcarving.thecloset.data.ToWearItem;

import java.util.ArrayList;

public class WidgetUpdateService extends IntentService {
    private static final String TAG = "WidgetUpdateService";
    public static final String WEATHER_EXTRA = "weather";
    public static final String TO_WEAR_ITEM_LIST_EXTRA = "to wear items";
    public static final String ACTION_UPDATE_WIDGET = "com.mcarving.thecloset.widget.updateWidget";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    // method to create intent for IntentService
    // and start the IntentService to retrieve weather data
    public static void startActionUpdateWidget(Context context,
                                               String weatherStatus,
                                               ArrayList<ToWearItem> toWearItems) {
        Intent i = new Intent(context, WidgetUpdateService.class);

        i.putExtra(WEATHER_EXTRA, weatherStatus);
        i.putParcelableArrayListExtra(TO_WEAR_ITEM_LIST_EXTRA, toWearItems);

        i.setAction(ACTION_UPDATE_WIDGET);
        context.startService(i);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // get data from intent
        // initiate widget update method

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET.equals(action)) {
                if (intent.hasExtra(WEATHER_EXTRA) && intent.hasExtra(TO_WEAR_ITEM_LIST_EXTRA)) {
                    String weatherStatus = intent.getStringExtra(WEATHER_EXTRA);
                    ArrayList<ToWearItem> toWearItems =
                            intent.getParcelableArrayListExtra(TO_WEAR_ITEM_LIST_EXTRA);
                    updateWidget(weatherStatus, toWearItems);
                }
            }
        }

    }

    // update widget with the new weather information
    private void updateWidget(String weatherStatus, ArrayList<ToWearItem> toWearItems) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetsIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, ClosetWidgetProvider.class));

        ClosetWidgetProvider.updateClosetWidgets(this,
                appWidgetManager, appWidgetsIds, weatherStatus, toWearItems);
    }
}
