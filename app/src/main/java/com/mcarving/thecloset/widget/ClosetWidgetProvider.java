package com.mcarving.thecloset.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.mcarving.thecloset.R;
import com.mcarving.thecloset.ToWearActivity;
import com.mcarving.thecloset.data.ToWearItem;

import java.util.ArrayList;

public class ClosetWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "ClosetWidgetProvider";

    public static ArrayList<ToWearItem> toWearList;
    private static String weatherStatus;


    // update widget with the weather inforamtion and toWearItems
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String weatherInfo,
                                ArrayList<ToWearItem> toWearItems) {

        weatherStatus = weatherInfo;
        toWearList = toWearItems;
        String currentDate = "Today";

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if (weatherStatus == null || weatherStatus.isEmpty()) {
            weatherStatus = "Weather information not available";
        }
        remoteViews.setTextViewText(R.id.tv_widget_weather, weatherStatus);

        if (toWearList != null && !toWearList.isEmpty()) {
            currentDate = currentDate + " - " + toWearList.get(0).getDateString();
        }
        remoteViews.setTextViewText(R.id.tv_widget_date, currentDate);

        Intent intent = new Intent(context, ClosetWidgetService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ClosetRemoteViewsFactory.TO_WEAR_ITEMS_EXTRA, toWearList);
        intent.putExtra(ClosetRemoteViewsFactory.DATA_EXTRA, bundle);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view);
        remoteViews.setRemoteAdapter(R.id.list_view, intent);

        //Set the ToWearActivity intent to launch when clicked
        Intent appIntent = new Intent(context, ToWearActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.closet_widget, appPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void updateClosetWidgets(Context context,
                                           AppWidgetManager appWidgetManager,
                                           int[] appWidgetIds,
                                           String weatherInfo,
                                           ArrayList<ToWearItem> toWearItems) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, weatherInfo, toWearItems);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // start the intent service update widget action,
        // the service takes care of updating the widgets UI
        WidgetUpdateService.startActionUpdateWidget(context, weatherStatus, toWearList);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated

    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
