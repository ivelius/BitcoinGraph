package com.example.yanbraslavski.bitcoingraph.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by yan.braslavski on 8/18/16.
 */

public class BitcoinApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        //It is perfectly fine to retain a static reference to app context
        //google it if you have doubts...
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
