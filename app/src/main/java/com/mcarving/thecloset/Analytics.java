package com.mcarving.thecloset;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    // get the username if the user login successfully
    public static void logEventLoginMainActivity(Context context, String username){
        Bundle params = new Bundle();
        params.putString("username", username);
        FirebaseAnalytics.getInstance(context)
                .logEvent("username", params);
    }

    // collect category name and clothe name of the selected content
    public static void logEventSelectContentClothListActivity(Context context,
                                                              String categoryName,
                                                              String clothName){
        Bundle params = new Bundle();
        params.putString("category_name", categoryName);
        params.putString("cloth_name", clothName);
        FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

    }

    // collection the number of items shown in ToWearActivity
    public static void logEventViewToWearActivity(Context context, int itemCount){
        Bundle params = new Bundle();
        params.putInt("item_count", itemCount);
        FirebaseAnalytics.getInstance(context)
                .logEvent("items_to_wear", params);
    }
}
