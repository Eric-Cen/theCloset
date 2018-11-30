package com.mcarving.thecloset;

import android.content.Context;

import android.widget.Toast;

public class Utils {

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }
}
