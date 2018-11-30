package com.mcarving.thecloset.data;

import android.net.Uri;

public final class DbContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract(){}

    public static final String CONTENT_AUTHORITY = "com.mcarving.thecloset";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

}
